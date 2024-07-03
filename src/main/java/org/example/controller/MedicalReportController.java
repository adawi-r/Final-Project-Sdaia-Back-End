package org.example.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.example.dao.MedicalReportDao;
import org.example.dto.MedicalReporFilterDto;
import org.example.dto.MedicalReportDto;
import org.example.exceptions.DataNotFoundException;
import org.example.mappers.MedicalReportMapper;
import org.example.models.MedicalReport;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/MEDICAL_REPORTS")
public class MedicalReportController {

    MedicalReportDao medicalReportDao = new MedicalReportDao();
    MedicalReportDto medicalReportDto = new MedicalReportDto();

    @Context
    UriInfo uriInfo;
    @Context
    HttpHeaders headers;

    public MedicalReportController() {
    }


//    //GET ALL MedicalReport
//    @GET
//    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
//    public Response selectAllMedicalReport() throws SQLException, ClassNotFoundException {
//        try {
//            GenericEntity<ArrayList<MedicalReport>> medicalReports = new GenericEntity<ArrayList<MedicalReport>>(medicalReportDao.selectAllMEDICAL_REPORTS(medicalReportDto)) {
//            };
//            return Response.ok(medicalReports, MediaType.APPLICATION_JSON).build();
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // GET MedicalReport BY ID
    @GET
    @Path("/{medical_report_id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
    public Response selectMedicalReportById(@PathParam("medical_report_id") int medical_report_id) throws SQLException, ClassNotFoundException {
        try {
            MedicalReport medicalReport = medicalReportDao.selectMedicalReportById(medical_report_id);
            if (medicalReport == null) {
                throw new DataNotFoundException("medical_report with ID " + medical_report_id + " not found");
            }

            MedicalReportDto medicalReportDto = MedicalReportMapper.INSTANCE.toMedicalReportDto(medicalReport);

            return Response.ok(medicalReportDto).build();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // select many things
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,"text/csv"})
    public Response getAllMedicalReports (
            @BeanParam MedicalReporFilterDto filter) throws SQLException, ClassNotFoundException {

        try {
            GenericEntity<ArrayList<MedicalReport>> meds = new GenericEntity<ArrayList<MedicalReport>>(medicalReportDao.selectAllMeds(filter)) {};

//            GenericEntity<ArrayList<MedicalReportDto>> meds = new GenericEntity<ArrayList<MedicalReportDto>>(medicalReport.selectAllMeds(filter)) {};

            if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf(MediaType.APPLICATION_XML))) {
                return Response
                        .ok(meds)
                        .type(MediaType.APPLICATION_XML)
                        .build();
            }
            else if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf("text/csv"))) {
                return Response
                        .ok(meds)
                        .type("text/csv")
                        .build();
            }
            return Response
                    .ok(meds, MediaType.APPLICATION_JSON)
                    .build();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    //Insert MedicalReport
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
    public Response InsertMedicalReport(MedicalReportDto medicalReportDto) throws SQLException, ClassNotFoundException {
        try {
            MedicalReport medicalReport = MedicalReportMapper.INSTANCE.toMedicalReportModel(medicalReportDto);

            medicalReportDao.InsertMedicalReport(medicalReport);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(medicalReport.getMedical_report_id())).build();
            return Response.created(uri).build();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // UPDATE schedule
    @PUT
    @Path("{medical_report_id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "text/csv"})
    public void updateMedicalReport(@PathParam("medical_report_id") int medical_report_id, MedicalReport medicalReport) throws SQLException, ClassNotFoundException {

        try {
            medicalReport.setMedical_report_id(medical_report_id);
            medicalReportDao.updateMedicalReport(medicalReport);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}