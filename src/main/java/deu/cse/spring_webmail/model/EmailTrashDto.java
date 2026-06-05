/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

/**
 *
 * @author suk22
 */
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTrashDto {
    private int id;
    private String userid;
    private String sender;
    private String subject;
    private String body;
    private String deletedDate;
}