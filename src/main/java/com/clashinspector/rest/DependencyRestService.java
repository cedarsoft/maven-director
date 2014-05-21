package com.clashinspector.rest;

import com.clashinspector.jacksonSerializer.InnerVersionClashSerializer;
import com.clashinspector.jacksonSerializer.OuterVersionClashSerializer;
import com.clashinspector.jacksonSerializer.ProjectSerializerForDependencyNodeWrapper;
import com.clashinspector.jacksonSerializer.VersionSerializer;
import com.clashinspector.model.InnerVersionClash;
import com.clashinspector.model.OuterVersionClash;
import com.clashinspector.model.Project;
import com.clashinspector.mojos.ClashSeverity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.glassfish.jersey.server.JSONP;
import org.eclipse.aether.version.Version;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: m
 * Date: 02.05.14
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
@Path( "dependencies" )
public class DependencyRestService {

  //Was pasiiert wenn zwei browserfenster geöffnet werden?    resultWrapper in sessionmap speichern


  private ViewScopeManager viewScopeManager = new ViewScopeManager();


  @GET
  @JSONP(queryParam="callback")
  @Produces("application/x-javascript")
  public String getAllDependencies(@QueryParam("callback") String callback,@QueryParam( "viewId" )int viewId,@QueryParam( "includedScope" ) List<String> includedScopes,@QueryParam( "includedScope" ) List<String> excludedScopes,@QueryParam( "includeOptional" ) boolean includeOptional)
  {
    //TODO Problem lösen, dass javascriopt speichert nur pro aufruf, also view ID jedesmal wieder verloren bei reload

    System.out.println("joo1 viewId:" + viewId);
    UserParameterWrapper userParameterWrapper = new UserParameterWrapper(includedScopes,excludedScopes,includeOptional);
    System.out.println("joo2");
    ObjectMapper mapper = new ObjectMapper(  );
    SimpleModule module = new SimpleModule( "MyModule", new org.codehaus.jackson.Version(1, 0, 0, null));
    System.out.println("joo3");
    module.addSerializer(Version.class, new VersionSerializer());
    module.addSerializer(Project.class, new ProjectSerializerForDependencyNodeWrapper());
    mapper.registerModule( module );
    System.out.println("joo4");
    //mapper.setVisibility( JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY );
    //mapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    String value = "";
    try
    {

      ViewScopeObject viewScopeObject = viewScopeManager.getViewScopeObject( viewId, userParameterWrapper) ;

      ResponseObject responseObject = new ResponseObject();

      responseObject.setResult( viewScopeObject.getClashCollectResultWrapper().getRoot() );
      responseObject.setUserParameterWrapper( viewScopeObject.getUserParameterWrapper() );
      responseObject.setViewId( viewScopeObject.getViewId() );

      value = mapper.writeValueAsString( responseObject  );
      System.out.println("joo6");
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

    return value;
  }



  @GET
  @Path("outerVersionClashes")
  @JSONP(queryParam="callback")
  @Produces("application/x-javascript")
  public String getClashList(@QueryParam("callback") String callback,@QueryParam( "viewId" )int viewId,@QueryParam( "includedScope" ) List<String> includedScopes,@QueryParam( "includedScope" ) List<String> excludedScopes,@QueryParam( "includeOptional" ) boolean includeOptional,@QueryParam( "clashSeverity" )ClashSeverity clashSeverity)
  {
    //TODO Problem lösen, dass javascriopt speichert nur pro aufruf, also view ID jedesmal wieder verloren bei reload
        clashSeverity = ClashSeverity.UNSAFE;
    System.out.println("joo1 viewId:" + viewId);
    UserParameterWrapper userParameterWrapper = new UserParameterWrapper(includedScopes,excludedScopes,includeOptional);
    System.out.println("joo2");
    ObjectMapper mapper = new ObjectMapper(  );
    SimpleModule module = new SimpleModule( "MyModule", new org.codehaus.jackson.Version(1, 0, 0, null));

    System.out.println("joo3");




    System.out.println("joo4");
    //mapper.setVisibility( JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY );
    //mapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    String value = "";
    try
    {

      ViewScopeObject viewScopeObject = viewScopeManager.getViewScopeObject( viewId, userParameterWrapper) ;



      ResponseObject responseObject = new ResponseObject();

      responseObject.setResult( viewScopeObject.getClashCollectResultWrapper().getOuterClashesForSeverityLevel(clashSeverity));
      responseObject.setUserParameterWrapper( viewScopeObject.getUserParameterWrapper() );
      responseObject.setViewId( viewScopeObject.getViewId() );



      module.addSerializer(Version.class, new VersionSerializer());
      module.addSerializer(OuterVersionClash.class, new OuterVersionClashSerializer(clashSeverity));
      module.addSerializer(InnerVersionClash.class, new InnerVersionClashSerializer());
      module.addSerializer(Project.class, new ProjectSerializerForDependencyNodeWrapper());

      mapper.registerModule( module );

      value = mapper.writeValueAsString( responseObject  );
      System.out.println("joo6");
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

    return value;
  }

   /*
  @Path("clashes")
  @GET
  @JSONP(queryParam="callback")
  @Produces("application/x-javascript")
  public String getClashList(@QueryParam("callback") String callback,@QueryParam( "clashSeverity" )ClashSeverity clashSeverity)
  {
    System.out.println("Clashseverity: "+clashSeverity);
    ObjectMapper mapper = new ObjectMapper(  );
    SimpleModule module = new SimpleModule( "MyModule", new org.codehaus.jackson.Version(1, 0, 0, null));

    module.addSerializer(Version.class, new VersionSerializer());
    mapper.registerModule( module );
    //mapper.setVisibility( JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY );
    //mapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    String value = "";
    try
    {
      value = mapper.writeValueAsString( clashCollectResultWrapper.getOuterVersionClashList() );

    }
    catch (Exception e)
    {
      System.out.println(e);
    }

    return value;
  }



             */



}

