package org.example.controller;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.example.dao.ConsultationDao;
import org.example.dto.ConsultationDto;
import org.example.dto.ConsultationFilterDto;
import org.example.exceptions.DataNotFoundException;
import org.example.mappers.ConsultationMapper;
import org.example.models.Consultation;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;


@Path("/CONSULTATIONS")
public class ConsultationController {

    ConsultationDao consultationDao = new ConsultationDao();
    ConsultationDto consultationDto = new ConsultationDto();

    @Context
    UriInfo uriInfo;
    @Context
    HttpHeaders headers;

    public ConsultationController() {
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,"text/csv"})
    public Response getAllConsults(
            //BeanParam hss object that contains more than on parameter
            //نجمع كل البراميترز داخل الكلاس ConsultFilterDto ونبعث الاوبجت filter
            @BeanParam ConsultationFilterDto filter ) throws SQLException, ClassNotFoundException {

        try {
            GenericEntity<ArrayList<Consultation>> consultation = new GenericEntity<ArrayList<Consultation>>(consultationDao.selectAllConsults(filter)) {};
            if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf(MediaType.APPLICATION_XML))) {
                return Response
                        .ok(consultation)
                        .type(MediaType.APPLICATION_XML)
                        .build();
//
            }else if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf("text/csv"))) {
                return Response
                        .ok(consultation)
                        .type("text/csv")
                        .build();
            }

            return Response
                    .ok(consultation, MediaType.APPLICATION_JSON)
                    .build();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //GET ALL Consultation

//    @GET
//    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
//    public Response selectAllConsultation() throws SQLException, ClassNotFoundException {
//        try {
//            GenericEntity<ArrayList<Consultation>> consultation = new GenericEntity<ArrayList<Consultation>>(consultationDao.selectAllConsultation(consultationDto)) {
//            };
//            return Response.ok(consultation, MediaType.APPLICATION_JSON).build();
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

 // GET Consultation BY ID
    @GET
    @Path("/{consultation_id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
    public Response selectConsultationById(@PathParam("consultation_id") int consultation_id) throws SQLException, ClassNotFoundException {
        try {
            Consultation consultation = consultationDao.selectConsultationById(consultation_id);
            if (consultation == null) {
                throw new DataNotFoundException("Consultation with ID " + consultation_id + " not found");
            }

          ConsultationDto consultationDto = ConsultationMapper.INSTANCE.toConsultationDto(consultation);

            return Response.ok(consultationDto).build();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //Insert Schedule
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
    public Response InsertConsultation(ConsultationDto consultationDto) throws SQLException, ClassNotFoundException {
        try {
            Consultation consultation = ConsultationMapper.INSTANCE.toConsultationModel(consultationDto);

            consultationDao.InsertConsultation(consultation);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(consultation.getConsultation_id())).build();
            return Response.created(uri).build();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // UPDATE schedule
    @PUT
    @Path("{consultation_id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
    public void updateConsultation(@PathParam("consultation_id") int consultation_id, Consultation consultation) throws SQLException, ClassNotFoundException {

        try {
            consultation.setConsultation_id(consultation_id);
            consultationDao.updateConsultation(consultation);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE schedule
//    @DELETE
//    @Path("/{consultation_id}")
//    public Response deleteConsultation(@PathParam("consultation_id") int consultation_id) throws SQLException, ClassNotFoundException {
//        try {
//            consultationDao.deleteConsultation(consultation_id);
//            return Response.ok().build();
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
