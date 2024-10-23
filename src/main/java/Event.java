import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Event {
    private String eventId;
    private int capacity;
    private LocalDateTime eventDate;
    private List<String> registeredUsers;
    private Map<String, RegistrationStatus> userRegistrations;

    public Event(String eventId, int capacity, LocalDateTime eventDate) {
        this.eventId = eventId;
        this.capacity = capacity;
        this.eventDate = eventDate;
        this.registeredUsers = new ArrayList<>();
        this.userRegistrations = new HashMap<>();
    }

    public RegistrationResult registerUser(String userId) {
        if (userRegistrations.containsKey(userId)) {

            //Prevent Re-adding of Registered users
            if(userRegistrations.get(userId) == RegistrationStatus.CONFIRMED){
                return new RegistrationResult(false, "Already registered");
            }
            // Avoid re-adding the waitlisted users
            if(userRegistrations.get(userId) == RegistrationStatus.WAITLIST){
                return new RegistrationResult(false, "Already in the waitlist");
            }
        }

        if (registeredUsers.size() < capacity) {
            userRegistrations.put(userId, RegistrationStatus.WAITLIST);
            return new RegistrationResult(false, "Event is full");
        }

        registeredUsers.add(userId);
        userRegistrations.put(userId, RegistrationStatus.CONFIRMED);
        
        return new RegistrationResult(true, "Registration successful");
    }

    public boolean cancelRegistration(String userId) {

        //Unregistered users - return false if not found
        if(!userRegistrations.containsKey((userId))){
            return false;
        }
        if (userRegistrations.get(userId) == RegistrationStatus.CONFIRMED) {
            userRegistrations.remove(userId);
            registeredUsers.remove(userId); // Make sure we remove from the registered users as well.
            // Even after cancellation we need to run the process
            processWaitlist();
            return true;
        }
        if (userRegistrations.get(userId) == RegistrationStatus.WAITLIST) {
            userRegistrations.remove(userId);
            // Successfully removing the user from the waitlist
            return true;
        }
        return false;
    }

    public void processWaitlist() {
        // Only promote if there is enough capacity in the event
        if(registeredUsers.size() < capacity){
        for (Map.Entry<String, RegistrationStatus> entry : userRegistrations.entrySet()) {
            if (entry.getValue() == RegistrationStatus.WAITLIST) {
                entry.setValue(RegistrationStatus.CONFIRMED);
                break; // we need to allow only one user at a time
            }
            }
        }
    }

    public List<String> getRegisteredUsers() {
        return registeredUsers;
    }
}

enum RegistrationStatus {
    CONFIRMED, WAITLIST
}

class RegistrationResult {
    private boolean success;
    private String message;

    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}