package bd.edu.seu.digitalhubpro.authentication.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Document
public class Member {
    @Id
    private String id;
    private String memberId;
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String photo;
    private List<String> roles;
    @Transient
    private String photosImagePath;

    public Member() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = (new BCryptPasswordEncoder()).encode(password);
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setPhotosImagePath(String photosImagePath) {
        this.photosImagePath = photosImagePath;
    }

    public String getPhotosImagePath() {
        return this.id != null && this.photo != null ? "member-photos/" + this.id + "/" + this.photo : "Images/avatar-default.jpg";
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

