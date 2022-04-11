package com.example.files;

import com.example.models.Appointment;
import com.example.models.Doctor;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Component
public class PdfConverter {
    final static String FONT = "/static/fonts/Formular.ttf";

    public void createPDF(Appointment appointment) throws IOException, DocumentException {

        Document document = new Document();
        PdfWriter.getInstance(document,
                new FileOutputStream("appointmentFiles/appointment" + appointment.getId() + ".pdf"));

        Rectangle one = new Rectangle(297,382);
        document.setPageSize(one);
        document.setMargins(20, 20, 10, 10);

        document.open();

        // Fonts
        BaseFont formular = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font fontHeader = new Font(formular, 15.3F, Font.NORMAL);
        Font fontText = new Font(formular, 12.5F, Font.NORMAL);
        Font fontBold = new Font(formular, 12.5F, Font.UNDERLINE);

        Paragraph address = new Paragraph("Талон на приём к врачу\n№" + appointment.getId(), fontHeader);
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(15f);
        document.add(address);

        String para1 = "\"RecoveryMed\" клиника на Электрозаводской \n Москва, Большая Семеновская ул, д. 38";
        Paragraph talon = new Paragraph(para1, fontText);
        talon.setAlignment(Element.ALIGN_CENTER);
        talon.setSpacingAfter(15f);
        document.add(talon);

        Date date = appointment.getDate();
        String dateInfo = new SimpleDateFormat("dd MMMMM yyyy").format(date);
        String dayInfo = new SimpleDateFormat("EEEEE").format(date);
        String timeInfo = appointment.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        Paragraph time = new Paragraph(dateInfo + ",\n" + dayInfo + "\n" + timeInfo, fontBold);
        time.setAlignment(Element.ALIGN_CENTER);
        time.setSpacingAfter(15f);
        document.add(time);

        Doctor appointmentDoctor = appointment.getDoctor();
        String specialities = appointmentDoctor.getSpecialities().toString();
        String doctorInfo = specialities.substring(1, specialities.length() - 1);
        String doc = doctorInfo + "\n" + appointmentDoctor.getSurname() + " " + appointmentDoctor.getName() + " " + appointmentDoctor.getPatronymic();
        Paragraph doctor = new Paragraph(doc, fontText);
        doctor.setAlignment(Element.ALIGN_CENTER);
        doctor.setSpacingAfter(10f);
        document.add(doctor);

        Paragraph cabinet = new Paragraph("Кабинет №317", fontBold);
        cabinet.setAlignment(Element.ALIGN_CENTER);
        cabinet.setSpacingAfter(15f);
        document.add(cabinet);

        Paragraph info = new Paragraph("Если вы не можете прийти на приём в указана время, сообщите об этом по телефону 8 (999) 123-45-67", fontText);
        info.setAlignment(Element.ALIGN_CENTER);
        document.add(info);

        document.close();
    }

}
