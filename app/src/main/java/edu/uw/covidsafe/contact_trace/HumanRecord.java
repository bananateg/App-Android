package edu.uw.covidsafe.contact_trace;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "human_record_table")
public class HumanRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "phoneNumber")
    private String phoneNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ColumnInfo(name = "email")
    private String email;

    @NonNull
    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(@NonNull String imgUri) {
        this.imgUri = imgUri;
    }

    @ColumnInfo(name = "imgUri")
    private String imgUri;

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HumanRecord(@NonNull String phoneNumber, String name, String imgUri, String email) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.imgUri = imgUri;
        this.email = email;
    }

    public String toString() {
        return this.name + " ("+this.phoneNumber+")\n";
    }
}
