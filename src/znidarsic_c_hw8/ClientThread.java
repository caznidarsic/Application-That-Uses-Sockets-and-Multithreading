package znidarsic_c_hw8;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.InterruptedIOException;
import java.io.IOException;
//import java.util.regex.Pattern;
import edu.jhu.en605681.*;
import java.time.LocalDateTime;
import java.util.Arrays;



public class ClientThread extends Thread {
	// create LocalDateTime object to get today's date
	private LocalDateTime localDateTime = LocalDateTime.now();
	private Socket socket;
	
	public ClientThread(Socket clientSocket) {
		super();
		this.socket = clientSocket;
	}
	
	public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        
        try {
        	socket.setSoTimeout(40000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out.println("You have reached the server");
            
            String inputLine = null;
            while (!socket.isClosed()) {
            	inputLine = in.readLine();
            	if (inputLine != null) {
            		
            		// check that input has correct number of arguments
            		int argIndex = 0;
            		String[] args = new String[] {"", "", "", "", ""};
            		boolean tooManyInputs = false;
            		for (int x = 0; x < inputLine.length(); x++) {
            			if (inputLine.charAt(x) == '&') {
            				if (argIndex == 4) {
            					tooManyInputs = true;
            					break;
            				}
            				else {
                				argIndex++;
            				}
            			}
            			else {
            				args[argIndex] = args[argIndex] + inputLine.charAt(x);
            			}
            		}
            		
            		if (tooManyInputs) {
            			out.println("-0.01&Too many arguments. Please enter input in the format: hike_id&duration&begin_month&begin_day&begin_year");
            		}
            		else if (argIndex < 4) {
            			out.println("-0.01&Too few arguments.  Please enter input in the format: hike_id&duration&begin_month&begin_day&begin_year");
            		}
            		else if (!args[0].matches("^\\d{1,2}$")) {
            			out.println("-0.01&The field \"hike_id\" is formatted incorrectly. Proper format is: \"##\" or \"#\"");
            		}
            		else if (!args[1].matches("^\\d{1,2}$")) {
            			out.println("-0.01&The field \"duration\" is formatted incorrectly. Proper format is: \"##\" or \"#\"");
            		}
            		else if (!args[2].matches("^\\d{1,2}$")) {
            			out.println("-0.01&The field \"begin_month\" is formatted incorrectly. Proper format is: \"MM\" or \"M\"");
            		}
            		else if (!args[3].matches("^\\d{1,2}$")) {
            			out.println("-0.01&The field \"begin_day\" is formatted incorrectly. Proper format is: \"DD\" or \"D\"");
            		}
            		else if (!args[4].matches("^\\d{4}$")) {
            			out.println("-0.01&The field \"begin_year\" is formatted incorrectly. Proper format is: \"YYYY\"");
            		}
            		else {
            			// create BookingDay object
            			BookingDay bookingDay = new BookingDay(Integer.valueOf(args[4]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
            			
        				if (bookingDay.before(new BookingDay(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()))) {
        					out.println("-0.01&The date entered has passed.");
        				}
        				else if (Integer.valueOf(args[0]) >= HikeType.values().length) {
        					out.println("-0.01&The field \"hike_id\" is greater than allowed. There are " + HikeType.values().length + " different hikes.");
        				}
            			else if (bookingDay.getValidation() != "VALID") {
        					out.println("-0.01&" + bookingDay.getValidation());
        				}
        				else {
                			// create Rates object
                			Rates rates = new Rates(HikeType.values()[Integer.valueOf(args[0])]);
        					rates.setBeginDate(bookingDay);
        					rates.setDuration(Integer.valueOf(args[1]));
        					
        					boolean invalidDuration = true;
        					for (int x : rates.getDurations()) {
        						if (x == Integer.valueOf(args[1])) {
        							invalidDuration = false;
        						}
        					}
        					
        					if (rates.isValidDates()) {
        						out.println(String.valueOf(rates.getCost()) + "&Quoted Rate");
        					}        				
        					else if (invalidDuration) {
            					out.println("-0.01&The field \"duration\" is invalid for hike_id: " + args[0] + ". This hike is offered for durations: " + Arrays.toString(rates.getDurations()));
            				}
        					else {
        						out.println("-0.01&" + rates.getDetails() + ". The hiking season begins on " + rates.getSeasonStartMonth() + "/"  + rates.getSeasonStartDay() + " and ends on " + rates.getSeasonEndMonth() + "/" + rates.getSeasonEndDay());
        					}
        				}
            		}
            		
            		break;
            	}
            }
            
            
        } catch (InterruptedIOException iioe) {
//        	System.out.println("Connection timed out due to inactivity");
        } catch (IOException e) {
        	System.err.println("IOException thrown");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("IOException thrown");
                in = null;
                out = null;
                socket = null;
            }
        }
        
	}
	
}
