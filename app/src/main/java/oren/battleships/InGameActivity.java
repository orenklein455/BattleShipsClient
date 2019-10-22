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

import java.util.Map;
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
    private Point first_point, second_point;
    private ScheduledExecutorService exec;
    private String TAG = InGameActivity.class.getSimpleName();

    @Override
    //this function defines 2 boards (model), draws 2 boards (view), run checkStatus
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        sent_my_board = false;

        tableMyBoard = (TableLayout) findViewById(R.id.tableMyBoard);
        tableOpponentBoard = (TableLayout) findViewById(R.id.opponent_table);
        status_display = (TextView) findViewById(R.id.status_display);

        setBoardDefinition(false); //create opponent's board (model)
        setBoardDefinition(true); //create my board (model)

        this.roomNumber = getIntent().getIntExtra("ROOM_NUMBER", -1); //get room's number from previous activity
        this.user = getIntent().getStringExtra("USER"); //get user's name from previous activity

        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().setBoardOwner(user);
        MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getBoard().setBoardRoom(roomNumber);

        exec = Executors.newSingleThreadScheduledExecutor(); //create a new executor
        exec.scheduleWithFixedDelay(new Runnable() { //create a new runnable and run it every second
            @Override
            public void run() {
                    new checkStatus().execute(); //create a new task and run it
                }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        DrawBoard(true, this.roomNumber); //draw my board (view)
        DrawBoard(false, this.roomNumber); //draw opponent's board (view)

    }


    //this function defines board
    protected void setBoardDefinition(boolean isMyBoard) {
        current_ship = 0;
        final TableLayout currentBoardGrid;
        final Board currentBoardData;

        if (isMyBoard) {
            currentBoardGrid = tableMyBoard; //check whether it's my board or the opponent's one
            currentBoardData = MyApplication.getInstance().getMyAllRooms()[InGameActivity.this.roomNumber].getGame().getBoard();

        }
        else {
            currentBoardGrid = tableOpponentBoard;
            currentBoardData = MyApplication.getInstance().getMyAllRooms()[InGameActivity.this.roomNumber].getGame().getOpponentBoard();
        }

        for (int i = 0; i < currentBoardGrid.getChildCount(); i++) { //for each row

            TableRow row = (TableRow) currentBoardGrid.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) { //for each button
                Button btn = (Button) row.getChildAt(j);

                if (isMyBoard) { //enable or disable button, add a relevant tag
                    btn.setEnabled(true);
                    btn.setTag(i + "_" + j + "_" + "player");
                } else {
                    btn.setEnabled(false);
                    btn.setTag(i + "_" + j + "_" + "opponent");
                }

                btn.setOnClickListener(new View.OnClickListener() { //set a listener

                    @Override
                    public void onClick(View view) {
                        Button buttonPressed = (Button) view;
                        if (buttonPressed.getTag() != null) {

                            //check which board and which cell were clicked.
                            String button_tag = buttonPressed.getTag().toString();
                            String buttonXY[] = button_tag.split("_");
                            if (buttonXY.length == 3) {
                                int button_x = Integer.parseInt(buttonXY[0]);
                                int button_y = Integer.parseInt(buttonXY[1]);
                                String board_owner = buttonXY[2];

                                //if board is mine and empty\preparation,
                                if (board_owner.equals("player") && (currentBoardData.getState() == Board.BoardStateEnum.EMPTY || currentBoardData.getState() == Board.BoardStateEnum.PREPARATION)) {
                                    if (currentBoardData.getCells()[button_x][button_y].getState() == Cell.StateEnum.EMPTY) { //if cell is empty

                                        //the next checks are general (it does fit a random ship number\size)
                                        if (currentBoardData.getPreparationState()[current_ship] == 0) first_point = new Point(button_x,button_y);
                                        if (currentBoardData.getPreparationState()[current_ship] == 1) {
                                            double distance = Math.hypot(first_point.x - button_x, first_point.y - button_y); //hypot is sum of the parameters' root
                                            if (distance != 1) {
                                                showBadLocationMessage();
                                                return;
                                            }
                                            second_point = new Point(button_x,button_y);
                                        }

                                        if (currentBoardData.getPreparationState()[current_ship] > 1) {
                                            if (((first_point.x == second_point.x) && (second_point.x == button_x)) || ((first_point.y == second_point.y) && (second_point.y == button_y))) {
                                                double distance1 = Math.hypot(first_point.x - button_x, first_point.y - button_y);
                                                double distance2 = Math.hypot(second_point.x - button_x, second_point.y - button_y);

                                                if (distance1 == 1) first_point.set(button_x,button_y);
                                                else if (distance2 == 1) second_point.set(button_x,button_y);
                                                else {
                                                    showBadLocationMessage();
                                                    return;
                                                }
                                            }
                                            else {
                                                showBadLocationMessage();
                                                return;
                                            }
                                        }

                                        currentBoardData.getCells()[button_x][button_y].setState(Cell.StateEnum.SHIP_PART); //put ship in the cell (model)

                                        //if ship is not completed yet
                                        if (currentBoardData.getPreparationState()[current_ship] < currentBoardData.getShipDesiredLength()[current_ship]) {
                                            currentBoardData.getPreparationState()[current_ship]++; //update ship's preparation state
                                            if (current_ship + 1 == currentBoardData.getPreparationState().length) //update board's state
                                                currentBoardData.setState(Board.BoardStateEnum.READY);
                                            else currentBoardData.setState(Board.BoardStateEnum.PREPARATION);
                                        }

                                    } else { //if cell isn't empty
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

                                if (board_owner.equals("opponent") && currentBoardData.getState().equals(Board.BoardStateEnum.READY)) {
                                    //if cell is empty, set to bombed
                                    if (currentBoardData.getCells()[button_x][button_y].getState().equals(Cell.StateEnum.EMPTY))
                                        currentBoardData.getCells()[button_x][button_y].setState(Cell.StateEnum.BOMBED);
                                        //if cell is ship, set to bombed ship
                                    else if (currentBoardData.getCells()[button_x][button_y].getState() == Cell.StateEnum.SHIP_PART)
                                        currentBoardData.getCells()[button_x][button_y].setState(Cell.StateEnum.BOMBED_SHIP_PART);
                                        //if cell is bombed or bombedShip, showMessage (it's possible to delete the condition, assuming we have 4 states)
                                    else if (currentBoardData.getCells()[button_x][button_y].getState() == Cell.StateEnum.BOMBED || currentBoardData.getCells()[button_x][button_y].getState() == Cell.StateEnum.BOMBED_SHIP_PART) {
                                        showBombedAlreadyMessage();
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
                board_to_send = MyApplication.getInstance().getMyAllRooms()[InGameActivity.this.roomNumber].getGame().getBoard();
                board_to_send.setBoardOwner(user);
            } else if (args0[0].equals("opBoard")) board_to_send = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard();

            board_to_send.setBoardSender(user);
            board_to_send.setBoardRoom(roomNumber);

            Gson gson = new Gson();
            jsonStrSent = gson.toJson(board_to_send);
            String jsonStrReceived = null;

            try {
                Map result = apiConsumer.sendBoard(board_to_send);
                jsonStrReceived = (String) result.get("message");
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
                Map result = apiConsumer.checkStatus(user, roomNumber);
                jsonStr = (String) result.get("message");

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

    private void showBadLocationMessage() {
        Toast.makeText(this, R.string.bad_location, Toast.LENGTH_SHORT).show();
    }

    private void showBombedAlreadyMessage () {
        Toast.makeText(this, R.string.bombed_already, Toast.LENGTH_SHORT).show();
    }

    //enable the button to be clickable or disable it
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

    //timer for the checkStatus to be scheduled
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

    //this function draws a board (my one or the opp's one)
    public void DrawBoard(boolean isMyBoard, int roomNumber) {
        Board currentBoardData;
        TableLayout currentBoardGrid;
        if (isMyBoard) {
            currentBoardData = MyApplication.getInstance().getMyAllRooms()[InGameActivity.this.roomNumber].getGame().getBoard();
            currentBoardGrid = tableMyBoard;
            if ((currentBoardData.getState() == Board.BoardStateEnum.EMPTY || currentBoardData.getState() == Board.BoardStateEnum.PREPARATION) && !exec.isShutdown()) //if board isn't ready
            {
                current_ship = currentBoardData.getPreparationNeededShip();     //get an incomplete ship
                if (current_ship != -1 && current_ship < currentBoardData.getPreparationState().length) //if there is an incomplete ship
                {
                    status_display.setText("You placed  " + (currentBoardData.getPreparationState()[current_ship]) + " cells out of " + (currentBoardData.getShipDesiredLength()[current_ship]) + " ship's cells");
                }
            }
            else { //else the board is ready
                if (!sent_my_board) { //if it's first run (I haven't sent the board yet) then send it..
                    status_display.setText("All ship have been placed successfully");
                    sent_my_board = true;
                    new sendBoard().execute("myBoard");
                }
                enableButtons(tableMyBoard, false);
            }
        }
        else { //else it's the opponent's board
            currentBoardData = MyApplication.getInstance().getMyAllRooms()[roomNumber].getGame().getOpponentBoard();
            currentBoardGrid = tableOpponentBoard;
        }

        Cell[][] myCells = currentBoardData.getCells();

        //draw the relevant board
        for (int i = 0; i < currentBoardGrid.getChildCount(); i++) {
            TableRow row = (TableRow) currentBoardGrid.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                Button btn = (Button) row.getChildAt(j);
                Cell x = myCells[i][j];
                                                //set the view of each cell, according to the data (in the model)
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
                        if (isMyBoard)
                            btn.setText("SP");

                        break;
                }
            }
        }
    }
}



