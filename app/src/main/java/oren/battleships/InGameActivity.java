package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import oren.battleships.model.Board;
import oren.battleships.model.Cell;

public class InGameActivity extends AppCompatActivity {

    private TableLayout tableMyBoard, tableOpponentBoard;
    private int roomNumber;
    private TextView status_display;
    private Board currentBoard ;
    int current_ship;
    private Point first_point, second_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        tableMyBoard = (TableLayout)findViewById(R.id.tableMyBoard);
        tableOpponentBoard = (TableLayout)findViewById(R.id.opponent_table);
        status_display = (TextView) findViewById(R.id.status_display);

        setBoardDefinition(false);
        setBoardDefinition(true);

        this.roomNumber = getIntent().getIntExtra("ROOM_NUMBER",-1);

        DrawBoard(true,this.roomNumber);
        DrawBoard(false,this.roomNumber);

        this.currentBoard = MyApplication.getInstance().getMyAllRooms()[InGameActivity.this.roomNumber].getGame().getBoard();
    }

    protected void setBoardDefinition(boolean myBoard) {
        current_ship = 0;
        final TableLayout currentBoardGrid;

        if (myBoard) currentBoardGrid = tableMyBoard;
        else currentBoardGrid = tableOpponentBoard;

        for (int i = 0; i < currentBoardGrid.getChildCount(); i++) {

            TableRow row = (TableRow) currentBoardGrid.getChildAt(i);

            for(int j = 0; j < row.getChildCount(); j++) {
                Button btn = (Button) row.getChildAt(j);

                if (myBoard) btn.setEnabled(true);
                else btn.setEnabled(false);
                btn.setTag(i +"_" + j);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button buttonPressed = (Button)view;
                        if(buttonPressed.getTag()!=null)
                        {
                            String button_tag = buttonPressed.getTag().toString();
                            String buttonXY[] = button_tag.split("_");
                            if (buttonXY!=null && buttonXY.length==2)
                            {
                                int button_x = Integer.parseInt(buttonXY[0]);
                                int button_y = Integer.parseInt(buttonXY[1]);
                                //TODO - pass this logic to board
                                //if board is empty OR board is in preparation and cell is empty
                                if (InGameActivity.this.currentBoard.getState() == Board.BoardStateEnum.EMPTY ||InGameActivity.this.currentBoard.getState() == Board.BoardStateEnum.PREPARATION && InGameActivity.this.currentBoard.getCells()[button_x][button_y].getState()== Cell.StateEnum.EMPTY)
                                {

                                        if (current_ship == 0 && currentBoard.getPreparationState()[current_ship] == 1) {
                                            double distance = Math.hypot(first_point.x - button_x, first_point.y - button_y);
                                            if (distance != 1) {
                                                showMessage();
                                                return;
                                            }
                                        }

                                        if (current_ship == 0 && currentBoard.getPreparationState()[current_ship] == 2) {
                                            if ((first_point.x == second_point.x && second_point.x != button_x) || (first_point.y == second_point.y && second_point.y != button_y))
                                             {
                                                showMessage();
                                                return;
                                            }
                                        }

                                        if (current_ship == 1 && currentBoard.getPreparationState()[current_ship] == 1) {
                                            double distance = Math.hypot(first_point.x - button_x, first_point.y - button_y);
                                            if (distance != 1) {
                                                showMessage();
                                                return;
                                            }

                                        }

                                    if (currentBoard.getShipDesiredLength()[current_ship] > 1 && currentBoard.getPreparationState()[current_ship] == 0) first_point = new Point(button_x,button_y);
                                    if (currentBoard.getShipDesiredLength()[current_ship] > 1 && currentBoard.getPreparationState()[current_ship] == 1) second_point = new Point(button_x,button_y);

                                    InGameActivity.this.currentBoard.getCells()[button_x][button_y].setState(Cell.StateEnum.SHIP_PART);

                                    if (InGameActivity.this.currentBoard.getPreparationState()[current_ship]<InGameActivity.this.currentBoard.getShipDesiredLength()[current_ship])
                                    {
                                        InGameActivity.this.currentBoard.getPreparationState()[current_ship]++;
                                        InGameActivity.this.currentBoard.setState(Board.BoardStateEnum.PREPARATION);
                                        if (current_ship + 1 == InGameActivity.this.currentBoard.getPreparationState().length) InGameActivity.this.currentBoard.setState(Board.BoardStateEnum.READY);
                                    }
                                }

                                //else if board is ready
                                else {
                                    try {
                                        if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().getState() == Board.BoardStateEnum.READY && MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getPlayer(MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getPlayerTurn()).getUserName().equals("abc"))
                                        {
                                          //  if (MyApplication.getInstance().getMyAllRooms()[this.roomNumber].getGame().getPlayers().) TODO - if it's my turn.
                                            //if cell is empty, set to bombed
                                            if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.EMPTY) MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].setState(Cell.StateEnum.BOMBED);
                                            //if cell is ship, set to bombed ship
                                            else if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.SHIP_PART) MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].setState(Cell.StateEnum.BOMBED_SHIP_PART);
                                            //if cell is bombed or bombedShip, showMessage
                                            else if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.BOMBED ||MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.BOMBED_SHIP_PART) showMessage2();
                                        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().setNextPlayerTurn();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                DrawBoard(true,InGameActivity.this.roomNumber);

                                DrawBoard(false,InGameActivity.this.roomNumber);
                            }

                        }
                    }
                });
            }
        }
    }

    private void showMessage() {
        Toast.makeText(this,R.string.bad_location,Toast.LENGTH_SHORT).show();
    }

    private void showMessage2() {
        Toast.makeText(this,R.string.bad_choice,Toast.LENGTH_SHORT).show();
    }

    public void DrawBoard(boolean myBoard,int roomNumber)
    {
        Board currentBoardData;
        TableLayout currentBoardGrid;
        if (myBoard)
        {
            currentBoardData =  MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard();
             currentBoardGrid = tableMyBoard;
            if (currentBoardData.getState()== Board.BoardStateEnum.EMPTY ||currentBoardData.getState()== Board.BoardStateEnum.PREPARATION ) //if board isn't ready
            {
                current_ship = currentBoardData.getPreparationNeededShip();     //get an incomplete ship
                if (current_ship!=-1 && current_ship<currentBoardData.getPreparationState().length) //if there is an incomplete ship
                     status_display.setText("You placed  " +  (currentBoardData.getPreparationState()[current_ship]) + " cells out of " + (currentBoardData.getShipDesiredLength()[current_ship]) + " ship's cells");
            }
            else {
                status_display.setText("All ship have been placed successfully"); //ToDo - update server's board state.
                MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().setState(Board.BoardStateEnum.READY);
            }
        }
        else
        {
            currentBoardData =  MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard();
            currentBoardGrid = tableOpponentBoard;
        }

        Cell [][]myCells = currentBoardData.getCells();

        for(int i = 0; i < currentBoardGrid.getChildCount(); i++)
        {
            TableRow row = (TableRow) currentBoardGrid.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++)
            {
                Button btn = (Button) row.getChildAt(j);
                Cell x=myCells[i][j];

                switch (x.getState())
                {
                    case BOMBED:
                        btn.setText("B");

                        break;
                    case BOMBED_SHIP_PART:
                        btn.setText("BSP");

                        break;
                    case EMPTY:
                        btn.setText(" ");

                        break;
                    case SHIP_PART:
                        if (myBoard)
                        btn.setText("SP");

                        break;

                }
                //if it's opp_board AND my_board is ready AND it's my_turn THE ToDo - make sure it's my turn
                if (!myBoard && MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().getState() == Board.BoardStateEnum.READY )
                    btn.setEnabled(true);

                if (myBoard && currentBoardData.getState() == Board.BoardStateEnum.READY)
                {
                    btn.setEnabled(false);
                }

            }
        }
    }
}






