package com.kodewiz.run.data;

import com.kodewiz.run.Utilities;

import java.io.Serializable;

public class Order implements Serializable{

    // $data = array('name' => $b_name, 'email' => $email, 'item' => $item_name, 'quantity' => $qty, 'flat' =>
    // $order_flat, 'landmark' => $order_landmark, 'address' => $b_address, 'area' => $order_area, 'zip' => $zip,
    // 'phone' => $phone, 'comment' => $comments, 'status' => $status);

    String name;
    String email;
    String item;
    String quantity;
    String address;
    String price;
    String description;
    String phone;
    String comment;
    String order_time;
    String coordinates;

    public Order(String name, String email, String item, String quantity, String address, String price, String description, String phone, String comment, String order_time, String coordinates) {
        this.name = name;
        this.email = email;
        this.item = item;
        this.quantity = quantity;
        this.address = address;
        this.price = price;
        this.description = description;
        this.phone = phone;
        this.comment = comment;
        this.order_time = order_time;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOrderTime() {
        return order_time;
    }

    public void setOrderTime(String order_time) {
        this.order_time = order_time;
    }

    public String getOrderTimeAsTimePassed(){
        if(order_time != null){
            return Utilities.getTimeSpanStringFromDate(order_time);
        }else{
            return null;
        }
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
