package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import oren.battleships.model.Board;
import oren.battleships.model.Cell;

import static oren.battleships.MainActivity.intent;

public class InGameActivity extends AppCompatActivity {

    private TableLayout tableMyBoard, tableOpponentBoard;
    private boolean sent_my_board;
    public String last_status, user;
    private int roomNumber, current_ship;
    private TextView status_display;
    private Board currentBoard;
    private Point first_point, second_point;
    private ScheduledExecutorService exec;
    private String TAG = InGameActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        sent_my_board = false;

        tableMyBoard = (TableLayout) findViewById(R.id.tableMyBoard);
        tableOpponentBoard = (TableLayout) findViewById(R.id.opponent_table);
        status_display = (TextView) findViewById(R.id.status_display);

        setBoardDefinition(false);
        setBoardDefinition(true);

        this.roomNumber = getIntent().getIntExtra("ROOM_NUMBER", -1);
        this.user = getIntent().getStringExtra("USER");

        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().setBoardOwner(user);
        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().setBoardRoom(roomNumber);

        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                    new checkStatus().execute();
                }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        DrawBoard(true, this.roomNumber);
        DrawBoard(false, this.roomNumber);
        this.currentBoard = MyApplication.getInstance().getMyAllRooms()[InGameActivity.this.roomNumber].getGame().getBoard();
    }

    protected void setBoardDefinition(boolean myBoard) {
        current_ship = 0;
        final TableLayout currentBoardGrid;

        if (myBoard) currentBoardGrid = tableMyBoard;
        else currentBoardGrid = tableOpponentBoard;

        for (int i = 0; i < currentBoardGrid.getChildCount(); i++) {

            TableRow row = (TableRow) currentBoardGrid.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                Button btn = (Button) row.getChildAt(j);

                if (myBoard) {
                    btn.setEnabled(true);
                    btn.setTag(i + "_" + j + "_" + "player");
                } else {
                    btn.setEnabled(false);
                    btn.setTag(i + "_" + j + "_" + "opponent");
                }

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button buttonPressed = (Button) view;
                        if (buttonPressed.getTag() != null) {
                            String button_tag = buttonPressed.getTag().toString();
                            String buttonXY[] = button_tag.split("_");
                            if (buttonXY.length == 3) {
                                int button_x = Integer.parseInt(buttonXY[0]);
                                int button_y = Integer.parseInt(buttonXY[1]);
                                String board_owner = buttonXY[2];
                                //TODO - pass this logic to board
                                if (board_owner.equals("player") && (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().getState() == Board.BoardStateEnum.EMPTY || MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().getState() == Board.BoardStateEnum.PREPARATION)) {
                                    if (currentBoard.getCells()[button_x][button_y].getState() == Cell.StateEnum.EMPTY) {
                                        if (current_ship == 0 && currentBoard.getPreparationState()[current_ship] == 1) {
                                            double distance = Math.hypot(first_point.x - button_x, first_point.y - button_y);
                                            if (distance != 1) {
                                                showMessage();
                                                return;
                                            }
                                        }

                                        if (current_ship == 0 && currentBoard.getPreparationState()[current_ship] == 2) {
                                            double distance = Math.hypot(first_point.x - button_x, first_point.y - button_y);
                                            double distance2 = Math.hypot(second_point.x - button_x, second_point.y - button_y);

                                            if (((first_point.x == second_point.x) && (second_point.x != button_x)) ||
                                                    ((first_point.y == second_point.y) && (second_point.y != button_y)) ||
                                                    (distance != 1 && distance2 != 1)) {
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

                                        if (currentBoard.getShipDesiredLength()[current_ship] > 1 && currentBoard.getPreparationState()[current_ship] == 0)
                                            first_point = new Point(button_x, button_y);
                                        if (currentBoard.getShipDesiredLength()[current_ship] > 1 && currentBoard.getPreparationState()[current_ship] == 1)
                                            second_point = new Point(button_x, button_y);

                                        InGameActivity.this.currentBoard.getCells()[button_x][button_y].setState(Cell.StateEnum.SHIP_PART);

                                        if (InGameActivity.this.currentBoard.getPreparationState()[current_ship] < InGameActivity.this.currentBoard.getShipDesiredLength()[current_ship]) {
                                            InGameActivity.this.currentBoard.getPreparationState()[current_ship]++;
                                            InGameActivity.this.currentBoard.setState(Board.BoardStateEnum.PREPARATION);
                                            if (current_ship + 1 == InGameActivity.this.currentBoard.getPreparationState().length)
                                                InGameActivity.this.currentBoard.setState(Board.BoardStateEnum.READY); //last ship has been located, then board is ready.
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),
                                                        "Ship is already placed in this cell",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        return;
                                    }
                                    DrawBoard(true, InGameActivity.this.roomNumber);
                                }

                                if (board_owner.equals("opponent") && MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().getState().equals(Board.BoardStateEnum.READY)) {
                                    //if cell is empty, set to bombed
                                    if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState().equals(Cell.StateEnum.EMPTY))
                                        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].setState(Cell.StateEnum.BOMBED);
                                        //if cell is ship, set to bombed ship
                                    else if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.SHIP_PART)
                                        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].setState(Cell.StateEnum.BOMBED_SHIP_PART);
                                        //if cell is bombed or bombedShip, showMessage
                                    else if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.BOMBED || MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getCells()[button_x][button_y].getState() == Cell.StateEnum.BOMBED_SHIP_PART) {
                                        showMessage2();
                                        return;
                                    }
                                    enableButtons(tableOpponentBoard, false);

                                    new sendBoard().execute("opBoard");
                                    DrawBoard(false, InGameActivity.this.roomNumber);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private class sendBoard extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... args0) {
            APIConsumer apiConsumer = new APIConsumer(getString(R.string.protocol), getString(R.string.server_ip), getString(R.string.server_port));
            String url = getString(R.string.protocol) + "://" + getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/sendBoard";

            String jsonStrSent = null;
            Board board_to_send = null;
            if (args0[0].equals("myBoard")) {
                board_to_send = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard();
                board_to_send.setBoardOwner(user);
            } else if (args0[0].equals("opBoard")) board_to_send = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard();

            board_to_send.setBoardSender(user);
            board_to_send.setBoardRoom(roomNumber);

            Gson gson = new Gson();
            jsonStrSent = gson.toJson(board_to_send);
            String jsonStrReceived = null;

            try {
                jsonStrReceived = apiConsumer.SendPost(url, jsonStrSent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonStrReceived);
            if (jsonStrReceived == null) {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class checkStatus extends AsyncTask<Void, Void, Integer> {
        private String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args0) {
            msg = user + "_" + roomNumber;
            APIConsumer apiConsumer = new APIConsumer(getString(R.string.protocol), getString(R.string.server_ip), getString(R.string.server_port));
            // Making a request to url and getting response
            String url = getString(R.string.protocol) + "://" + getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/checkStatus";

            String jsonStr = null;
            try {
                jsonStr = apiConsumer.SendPost(url, msg);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                if (sent_my_board) {

                    if (jsonStr.equals("No 2nd player") && !jsonStr.equals(last_status)) {
                        last_status = jsonStr;
                        return 1;
                    }

                    if (jsonStr.equals("waiting for player2's board") && !jsonStr.equals(last_status)) {
                        last_status = jsonStr;
                        return 2;
                    }

                    if (jsonStr.equals("OpTurn") && !jsonStr.equals(last_status)) {
                        last_status = jsonStr;
                        return 3;
                    }

                    if (jsonStr.equals("YourTurn") && (last_status == null || !last_status.equals("YourTurn"))) { //TODO - delete the null condition
                        last_status = jsonStr;
                        return 4;
                    }

                    if (jsonStr.equals("You win!") && !jsonStr.equals(last_status)) {
                        last_status = jsonStr;
                        return 5;
                    }

                    if (jsonStr.equals("Player2 wins!") && !jsonStr.equals(last_status)) {
                        last_status = jsonStr;
                        return 6;
                    }
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == null) return;
            if (result == 1) status_display.setText("Waiting for player2 to arrive...");

            else if (result == 2) status_display.setText("Waiting for player2 to locate ships...");

            else if (result == 3) {
                status_display.setText("Waiting for player2 move...");
                enableButtons(tableOpponentBoard, false);
            }

            else if (result == 4) {
                if (MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard().getBoardOwner().equals("opponent'sOne")) new getBoard().execute("opBoard");
                new getBoard().execute("myBoard");
                enableButtons(tableOpponentBoard, true);
                status_display.setText("It's your turn..Shoot!");
            }

            else if (result == 5) {
                if (!exec.isShutdown()) exec.shutdown();
                startTimer(5000, 900, "You win! :)");
            }

            else if (result == 6) {
                if (!exec.isShutdown()) exec.shutdown();
                startTimer(5000, 900, "You lose! :(");
            }
        }
    }

    private class getBoard extends AsyncTask<String, Void, String> {
        private String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args0) {
            APIConsumer apiConsumer = new APIConsumer(getString(R.string.protocol), getString(R.string.server_ip), getString(R.string.server_port));
            // Making a request to url and getting response
            String url = getString(R.string.protocol) + "://" + getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/getBoard";

            String jsonStr = null;
            try {
                if (!args0[0].equals("myBoard") && !args0[0].equals("opBoard")) return null;
                msg = user + "_" + roomNumber + "_" + args0[0];
                jsonStr = apiConsumer.SendPost(url, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Board received_board = null;
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                Gson gson = new Gson();
                Board received_board = gson.fromJson(jsonStr, Board.class);
                if (args0[0].equals("myBoard")) MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().setBoard(received_board);
                 else if (args0[0].equals("opBoard")) MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().setOpponentBoard(received_board);

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return args0[0];
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("myBoard"))
                DrawBoard(true, roomNumber);
        }
    }

    private void showMessage() {
        Toast.makeText(this, R.string.bad_location, Toast.LENGTH_SHORT).show();
    }

    private void showMessage2 () {
        Toast.makeText(this, R.string.bad_choice, Toast.LENGTH_SHORT).show();
    }

       private void enableButtons(TableLayout table, Boolean enabled) {
           Button btn;
           TableRow row;
           for (int i = 0; i < table.getChildCount(); i++) {
               row = (TableRow) table.getChildAt(i);
               for (int j = 0; j < row.getChildCount(); j++) {
                   btn = (Button) row.getChildAt(j);
                   btn.setEnabled(enabled);
               }
           }
       }

    private  void startTimer(final long milisec, long interval, final String text) {

        CountDownTimer cTimer = new CountDownTimer(milisec, interval) {

            public void onTick(long millisUntilFinished) {
                status_display.setText(text + "  " + "you will be moved to the lobby in " + millisUntilFinished / 1000 + " seconds.");
            }

            public void onFinish() {
                MyApplication.getInstance().getMyAllRooms()[roomNumber].resetRoom();
                intent = new Intent(InGameActivity.this, SelectRoomActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
                finish();
            }
        };
        cTimer.start();
    }

    public void DrawBoard(boolean myBoard, int roomNumber) {
        Board currentBoardData;
        TableLayout currentBoardGrid;
        if (myBoard) {
            currentBoardData = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard();
            currentBoardGrid = tableMyBoard;
            if ((currentBoardData.getState() == Board.BoardStateEnum.EMPTY || currentBoardData.getState() == Board.BoardStateEnum.PREPARATION) && !exec.isShutdown()) //if board isn't ready
            {
                current_ship = currentBoardData.getPreparationNeededShip();     //get an incomplete ship
                if (current_ship != -1 && current_ship < currentBoardData.getPreparationState().length)
                {
                    status_display.setText("You placed  " + (currentBoardData.getPreparationState()[current_ship]) + " cells out of " + (currentBoardData.getShipDesiredLength()[current_ship]) + " ship's cells");

                    //if there is an incomplete ship
                }
            } else {
                if (!sent_my_board) { //if it's first run (I haven't sent the board yet) then send it..
                    status_display.setText("All ship have been placed successfully");
                    sent_my_board = true;
                    new sendBoard().execute("myBoard");
                }

                enableButtons(tableMyBoard, false);
            }
        } else {
            currentBoardData = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard();
            currentBoardGrid = tableOpponentBoard;
        }

        Cell[][] myCells = currentBoardData.getCells();

        for (int i = 0; i < currentBoardGrid.getChildCount(); i++) {
            TableRow row = (TableRow) currentBoardGrid.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                Button btn = (Button) row.getChildAt(j);
                Cell x = myCells[i][j];

                switch (x.getState()) {
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
            }
        }
    }
}



