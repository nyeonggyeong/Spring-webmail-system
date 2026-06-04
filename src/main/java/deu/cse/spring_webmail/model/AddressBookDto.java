/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;
import lombok.Data;
/**
 *
 * @author suk22
 */
@Data

public class AddressBookDto {
    private int id;
    private String userid;
    private String name;
    private String email;
    private String phone;
}
