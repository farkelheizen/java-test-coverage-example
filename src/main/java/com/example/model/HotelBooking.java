package com.example.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class HotelBooking {
    private String bookingId;
    private String guestName;
    private String hotelName;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double pricePerNight;
    private boolean isPaid;

    public HotelBooking() {}

    public HotelBooking(String bookingId, String guestName, String hotelName, String roomNumber,
                        LocalDate checkIn, LocalDate checkOut, double pricePerNight, boolean isPaid) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.hotelName = hotelName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.pricePerNight = pricePerNight;
        this.isPaid = isPaid;
    }

    public double getTotalCost() {
        return ChronoUnit.DAYS.between(checkIn, checkOut) * pricePerNight;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}
