package com.mobileapp.polimi.maprojectg4.model;


import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OnlineGame {

    /** Server data */
    private static final String api_key = "APIKey c9157e69-5db6-484e-abfc-d3cc769593f7";
    private static final String serverUrl = "http://mobileapp16.bernaschina.com/api/room/group04";

    /** Game data */
    private static String gameUrl = null;
    private Piece.Team yourTeam;

    /** Attributes */
    private HttpURLConnection connection = null;
    private HttpURLConnection connectionMove = null;
    private char[] clickMove;

    /** Getter and setter*/
    public Piece.Team getYourTeam() {
        return yourTeam;
    }
    public static void setGameUrl(String gameUrl) {
        OnlineGame.gameUrl = gameUrl;
    }
    public void setYourTeam(Piece.Team yourTeam) {
        this.yourTeam = yourTeam;
    }
    public void setClickMove(char[] move) {
        this.clickMove = move;
    }
    public char[] getClickMove() {
        return clickMove;
    }
    public HttpURLConnection getConnection() {
        return connection;
    }
    public HttpURLConnection getConnectionMove() {
        return connectionMove;
    }

    public OnlineGame() {
        //this.yourTeam = null;
        this.clickMove = new char[5];
    }

    /** http GET and POST methods*/

    /**
     * Does the http GET request to the server; put the online game request in stack.
     * If there is an other request in the server stack the game is created and the functions returns the
     * game data.
     * @return array of strings with the game data:
     * the ID of the game
     * the url of the game
     * the colour of the team
     */
    public String[] getMatch() {

        String[] resp = new String[3];

        try {

            URL url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setRequestProperty( "Authorization", api_key);
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            InputStream in;
            JSONObject obj;
            String serverMess;

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
                serverMess = convertStreamToString(in);
            }else{
                in = connection.getInputStream();
                serverMess = convertStreamToString(in);
                try {
                    obj = new JSONObject(serverMess);

                    switch (connection.getResponseCode()) {

                        case 401:
                            resp[0] = "401";
                            resp[1] = connection.getResponseMessage();
                            break;

                        case 200:
                            resp[0] = "200";
                            try {
                                String gameID = obj.getString("game");
                                resp[1] = obj.getString("url");
                                resp[2] = obj.getString("color");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            resp[0] = Integer.toString(connection.getResponseCode());
                            in.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e1) {

            resp[0] = e1.getMessage();
        }

        return resp;
    }

    /**
     * Does the http GET request to the server to recieve the opponent move.
     * @return array of strings with the request data
     *  the response code
     *  the corresponding message
     */
    public String[] getMove(){

        String[] respMove = new String[2];

        try{

            URL url = new URL(gameUrl);
            connectionMove = (HttpURLConnection) url.openConnection();
            connectionMove.setRequestProperty( "Authorization", api_key);
            connectionMove.setRequestProperty("Accept", "application/json");
            connectionMove.setConnectTimeout(30000);
            connectionMove.connect();

            InputStream in;
            JSONObject obj;
            String serverMess;

            int status = connectionMove.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                in = connectionMove.getErrorStream();
                respMove[0] = convertStreamToString(in);

            }else{
                in = connectionMove.getInputStream();
                serverMess = convertStreamToString(in);
                try {
                    obj = new JSONObject(serverMess);

                    switch (connectionMove.getResponseCode()) {

                        case 401:
                            respMove[0] = "401";
                            respMove[1] = connectionMove.getResponseMessage();
                            break;

                        case 410:
                            respMove[0] = "410";
                            respMove[1] = connectionMove.getResponseMessage();
                            break;

                        case 200:

                            respMove[0] = "200";
                            try {
                                obj = new JSONObject(serverMess);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                respMove[1] = obj.getString("move");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            respMove[0] = Integer.toString(connectionMove.getResponseCode());
                            respMove[1] = connectionMove.getResponseMessage();
                            in.close();

                    }
                    return respMove;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {

            e.printStackTrace();
            respMove[0] = e.getMessage().toString();
        }
        return respMove;
    }

    /**
     * Does the http POST request to the server; send the move.
     * @param move string that encode the move
     * @return string with the response code from the server.
     */
    public String postMove(String move) {

        String respCode = null;

        try {

            URL url = new URL(gameUrl);
            connectionMove = (HttpURLConnection) url.openConnection();
            connectionMove.setRequestProperty( "Authorization", api_key);
            connectionMove.setRequestProperty("Accept", "q");
            connectionMove.setRequestProperty( "Content-Type", "text/plain" );
            connectionMove.setConnectTimeout(30000);
            connectionMove.setRequestMethod("POST");
            connectionMove.setDoOutput(true);
            connectionMove.setDoInput(true);

            OutputStream os = connectionMove.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(move);

            writer.flush();
            writer.close();
            os.close();

            connectionMove.connect();

            InputStream in;
            int status = connectionMove.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK){
                in = connectionMove.getErrorStream();
                return convertStreamToString(in);
            } else{
                in = connectionMove.getInputStream();
                respCode = "200";
            }

        } catch (IOException e) {
            e.printStackTrace();
            respCode = e.getMessage().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respCode;
    }

    /**
     * Converts the InputStream into a String
     * @param is InputStream
     * @return String corresponding to the InputStream
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
