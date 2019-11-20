package com.CRUDOps.Services;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;

@Path("/CRUDOps")
public class Services {
	
	@Path("/addStream={x}")
	@RolesAllowed("ADMIN")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void addStream(@PathParam("x") String x)
	{
		
		com.google.appengine.api.datastore.Key key=KeyFactory.createKey("SubjectStream", x);
		
		Entity e=new Entity(x);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			e.setProperty("stream", x);
			datastore.put(txn,e);
		}
		finally
		{
			if(txn.isActive())
			{
				txn.rollback();
			}
		}
		
	}
	
	@Path("/addCourse/stream={x},course={y}")
	@RolesAllowed("ADMIN")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void addCourses(@PathParam("x") String x, @PathParam("y") String y) throws EntityNotFoundException
	{
		String courseId=UUID.randomUUID().toString();
		Entity e=new Entity(KeyFactory.createKey("Course", x));
		
		DatastoreService datastore= DatastoreServiceFactory.getDatastoreService();
		Transaction txn=datastore.beginTransaction();
		
		com.google.appengine.api.datastore.Key key=KeyFactory.createKey("SubjectStream", y);
		
		
		try
		{
			Entity stream= datastore.get(txn,key);
			String streamName=(String) stream.getProperty("stream");
			e.setProperty("courseName", x);
			e.setProperty("courseId", UUID.randomUUID().toString());
			e.setProperty("stream", y);
			datastore.put(txn,e);
			
		}
		
		finally
		{
			if(txn.isActive())
			{
				txn.rollback();
			}
		}
	}
	
	@Path("/searchCourse={x}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchCourse(@PathParam("x") String x)
	{
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		String output = "";
		
		try {
			Entity e=datastore.get(KeyFactory.createKey("Course", x));
			output=e.toString();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(200).entity(output).build();
	}
	
	@Path("/searchStream={x}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchStream(@PathParam("x") String x)
	{
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		String output="";
		
		try {
			Entity e=datastore.get(KeyFactory.createKey("SubjectStream",x));
			String streamName=(String) e.getProperty("stream");
			Query q=new Query("Course").setFilter(new FilterPredicate("stream", FilterOperator.EQUAL, x));
			PreparedQuery pq=datastore.prepare(q);
			List<Entity> courses=pq.asList(FetchOptions.Builder.withLimit(10));
			
			for(Entity c: courses)
			{
				output=output+c.toString()+" : ";
			}
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(200).entity(output).build();
	}
	
	

}
