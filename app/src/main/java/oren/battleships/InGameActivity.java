package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import oren.battleships.model.Cell;


public class InGameActivity extends AppCompatActivity {

    TableLayout tableMyBoard;
    private int roomNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        tableMyBoard = (TableLayout)findViewById(R.id.tableMyBoard);

        this.roomNumber = getIntent().getIntExtra("ROOM_NUMBER",-1);
        if (this.roomNumber!=-1)
        {
            DrawBoard(this.roomNumber);

        }

//        ((Button)(((TableRow)tableMyBoard.getChildAt(2)).getChildAt(3))).setText("V");

    }

    public void DrawBoard(int roomNumber)
    {
        Cell [][]myCells = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().getCells();
//        myCells[2][0].setState(Cell.StateEnum.SHIP_PART);
//        myCells[2][1].setState(Cell.StateEnum.SHIP_PART);
//        myCells[2][2].setState(Cell.StateEnum.SHIP_PART);
//
//
//        myCells[0][1].setState(Cell.StateEnum.BOMBED_SHIP_PART);
//        myCells[1][1].setState(Cell.StateEnum.BOMBED_SHIP_PART);
//        myCells[1][2].setState(Cell.StateEnum.BOMBED_SHIP_PART);
        for(int i = 0; i < tableMyBoard.getChildCount(); i++)
        {
            //Remember that .getChildAt() method returns a View, so you would have to cast a specific control.
            TableRow row = (TableRow) tableMyBoard.getChildAt(i);
            //This will iterate through the table row.
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
                        btn.setText("SP");

                        break;

                }

                btn.setEnabled(false);
                //Do what you need to do.
            }
        }
    }
}
