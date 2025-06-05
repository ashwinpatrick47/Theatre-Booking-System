package model;

public class Event {
    private int id;
    private String title;
    private String venue;
    private String day;
    private double price;
    private int ticketsSold;
    private int totalTickets;
    private boolean enabled;


    public Event(int id, String title, String venue, String day, double price, int ticketsSold, int totalTickets, boolean enabled) {
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.day = day;
        this.price = price;
        this.ticketsSold = ticketsSold;
        this.totalTickets = totalTickets;
        this.enabled = enabled;
    }


    public int getRemainingTickets()
    {
        return totalTickets - ticketsSold;
    }

    @Override
    public String toString()
    {
        return "[" + id + "] " + title + " at " + venue + " on " + day + " - $" + price +
                " | Remaining: " + getRemainingTickets();
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public String getTitle()
    {
        return title;
    }

    public String getVenue() {
        return venue;
    }
    public String getDay() {
        return day;
    }
    public double getPrice() {
        return price;
    }

    public int getRemainingTicket(){
        return totalTickets - ticketsSold;
    }

    public int getId()
    {
        return id;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
