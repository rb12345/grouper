/**
 *
 */
package edu.internet2.middleware.grouper.webservicesClient;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import edu.internet2.middleware.grouper.webservicesClient.util.GeneratedClientSettings;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType;
import edu.internet2.middleware.grouper.ws.soap.xsd.GetGroupsLite;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsGetGroupsLiteResult;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsGroup;


/**
 * @author mchyzer
 *
 */
public class WsSampleGetGroupsLite implements WsSampleGenerated {
    /**
     * @param args
     */
    public static void main(String[] args) {
        getGroupsLite(WsSampleGeneratedType.soap);
    }

    /**
     * @see edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated#executeSample(edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType)
     */
    public void executeSample(WsSampleGeneratedType wsSampleGeneratedType) {
        getGroupsLite(wsSampleGeneratedType);
    }

    /**
     * @param wsSampleGeneratedType can run as soap or xml/http
     */
    public static void getGroupsLite(
        WsSampleGeneratedType wsSampleGeneratedType) {
        try {
            //URL, e.g. http://localhost:8091/grouper-ws/services/GrouperService
            GrouperServiceStub stub = new GrouperServiceStub(GeneratedClientSettings.URL);
            Options options = stub._getServiceClient().getOptions();
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(GeneratedClientSettings.USER);
            auth.setPassword(GeneratedClientSettings.PASS);
            auth.setPreemptiveAuthentication(true);

            options.setProperty(HTTPConstants.AUTHENTICATE, auth);
            options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(3600000));
            options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,
                new Integer(3600000));

            GetGroupsLite getGroupsLite = GetGroupsLite.class.newInstance();

            //version, e.g. v1_3_000
            getGroupsLite.setClientVersion(GeneratedClientSettings.VERSION);

            getGroupsLite.setActAsSubjectId("");
            getGroupsLite.setActAsSubjectIdentifier("");
            getGroupsLite.setActAsSubjectSourceId("");

            // check all
            getGroupsLite.setMemberFilter("All");

            getGroupsLite.setSubjectId("GrouperSystem");
            getGroupsLite.setSubjectIdentifier("");
            getGroupsLite.setSubjectSourceId("");
            getGroupsLite.setIncludeGroupDetail("F");
            getGroupsLite.setIncludeSubjectDetail("F");
            getGroupsLite.setSubjectAttributeNames("description");
            
            WsGetGroupsLiteResult wsGetGroupsLiteResult = stub.getGroupsLite(getGroupsLite)
                                                              .get_return();

            System.out.println(ToStringBuilder.reflectionToString(
                    wsGetGroupsLiteResult));

            WsGroup[] results = wsGetGroupsLiteResult.getWsGroups();

            if (results != null) {
                for (WsGroup wsGroup : results) {
                    System.out.println(ToStringBuilder.reflectionToString(
                            wsGroup));
                }
            }
            
            if (!StringUtils.equals("T", 
                wsGetGroupsLiteResult.getResultMetadata().getSuccess())) {
              throw new RuntimeException("didnt get success! ");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}