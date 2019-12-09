package org.eurekaclinical.eureka.client;

/*-
 * #%L
 * Eureka! Client
 * %%
 * Copyright (C) 2016 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eurekaclinical.common.comm.Role;
import org.eurekaclinical.common.comm.User;
import org.eurekaclinical.common.comm.clients.AuthorizingEurekaClinicalClient;
import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.eureka.client.comm.CohortDestination;
import org.eurekaclinical.eureka.client.comm.Destination;
import org.eurekaclinical.eureka.client.comm.DestinationType;
import org.eurekaclinical.eureka.client.comm.I2B2Destination;
import org.eurekaclinical.eureka.client.comm.Job;
import org.eurekaclinical.eureka.client.comm.JobMode;
import org.eurekaclinical.eureka.client.comm.JobSpec;
import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.eurekaclinical.eureka.client.comm.SourceConfig;
import org.eurekaclinical.eureka.client.comm.SourceConfigParams;
import org.eurekaclinical.eureka.client.comm.Statistics;
import org.eurekaclinical.eureka.client.comm.SystemPhenotype;
import org.eurekaclinical.standardapis.exception.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrew Post
 */
public class EurekaClient extends AuthorizingEurekaClinicalClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaClient.class);

    /*private static final GenericType<List<TimeUnit>> TimeUnitList = new GenericType<List<TimeUnit>>() {
    };
    private static final GenericType<List<FrequencyType>> FrequencyTypeList = new GenericType<List<FrequencyType>>() {
    };
    private static final GenericType<List<RelationOperator>> RelationOperatorList = new GenericType<List<RelationOperator>>() {
    };
    private static final GenericType<List<ThresholdsOperator>> ThresholdsOperatorList = new GenericType<List<ThresholdsOperator>>() {
    };
    private static final GenericType<List<ValueComparator>> ValueComparatorList = new GenericType<List<ValueComparator>>() {
    };*/
    private static final GenericType<List<SystemPhenotype>> SystemPhenotypeList = new GenericType<List<SystemPhenotype>>() {
    };
    private static final GenericType<List<Phenotype>> PhenotypeList
            = new GenericType<List<Phenotype>>() {
    };
    private static final GenericType<List<Role>> RoleList = new GenericType<List<Role>>() {
    };
    private static final GenericType<List<Job>> JobList = new GenericType<List<Job>>() {
    };
    private static final GenericType<List<JobMode>> JobModeList = new GenericType<List<JobMode>>() {
    };
    private static final GenericType<List<User>> UserList = new GenericType<List<User>>() {
    };
    private static final GenericType<List<SourceConfig>> SourceConfigList = new GenericType<List<SourceConfig>>() {
    };
    private static final GenericType<List<SourceConfigParams>> SourceConfigParamsList = new GenericType<List<SourceConfigParams>>() {
    };
    private static final GenericType<List<Destination>> DestinationList = new GenericType<List<Destination>>() {
    };
    private static final GenericType<List<CohortDestination>> CohortDestinationListType
            = new GenericType<List<CohortDestination>>() {
    };
    private static final GenericType<List<I2B2Destination>> I2B2DestinationListType
            = new GenericType<List<I2B2Destination>>() {
    };
    private static final GenericType<List<String>> SystemPhenotypeSearchResultsList = new GenericType<List<String>>() {
    };

    private final URI eurekaUrl;

    public EurekaClient(String inEurekaUrl) {
        super(null);
        this.eurekaUrl = URI.create(inEurekaUrl);
    }

    @Override
    protected URI getResourceUrl() {
        return this.eurekaUrl;
    }

    public void updateUser(User inUser, Long userId) throws ClientException {
        final String path = "/api/protected/users/" + userId;
        doPut(path, inUser);
    }

    public Long submitJob(JobSpec inUpload) throws ClientException {
        final String path = "/api/protected/jobs";
        URI jobUri = doPostCreate(path, inUpload);
        return extractId(jobUri);
    }

    public void upload(String fileName, String sourceId,
            String fileTypeId, InputStream inputStream)
            throws ClientException {
        String path = UriBuilder
                .fromPath("/api/protected/file/upload/")
                .segment(sourceId)
                .segment(fileTypeId)
                .build().toString();
        FormDataMultiPart part = new FormDataMultiPart();
        part.bodyPart(
                new FormDataBodyPart(
                        FormDataContentDisposition
                                .name("file")
                                .fileName(fileName)
                                .build(),
                        inputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        doPostMultipart(path, part);
    }

    public Job getJob(Long jobId) throws ClientException {
        final String path = "/api/protected/jobs/" + jobId;
        return doGet(path, Job.class);
    }
    
    public JobMode getJobMode(Long jobModeId) throws ClientException {
        final String path = "/api/protected/jobmodes/" + jobModeId;
        return doGet(path, JobMode.class);
    }
    
    public JobMode getJobModeByName(String name) throws ClientException {
        final String path = "/api/protected/jobmodes/byname/" + name;
        return doGet(path, JobMode.class);
    }

    public Statistics getJobStats(Long jobId, String propId) throws ClientException {
        if (jobId == null) {
            throw new IllegalArgumentException("jobId cannot be null");
        }
        UriBuilder uriBuilder = UriBuilder.fromPath("/api/protected/jobs/{arg1}/stats/");
        if (propId != null) {
            uriBuilder = uriBuilder.segment(propId);
        }

        return doGet(uriBuilder.build(jobId).toString(), Statistics.class);
    }

    public List<Job> getJobs() throws ClientException {
        final String path = "/api/protected/jobs";
        return doGet(path, JobList);
    }
    
    public List<JobMode> getJobModes() throws ClientException {
        final String path = "/api/protected/jobmodes";
        return doGet(path, JobModeList);
    }

    public List<Job> getJobsDesc() throws ClientException {
        final String path = "/api/protected/jobs";
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("order", "desc");
        return doGet(path, JobList, queryParams);
    }

    public List<Job> getLatestJob() throws ClientException {
        final String path = "/api/protected/jobs/latest";
        return doGet(path, JobList);
    }

    public List<Phenotype> getPhenotypes(String[] inKeys, boolean summarized) throws ClientException {
        List<Phenotype> result = new ArrayList<>();
        if (inKeys != null) {
            List<String> userPhenotypes = new ArrayList<>();
            List<String> systemPhenotypes = new ArrayList<>();
            for (String key : inKeys) {
                if (key.startsWith("USER:")) {
                    userPhenotypes.add(key);
                } else {
                    systemPhenotypes.add(key);
                }
            }
            if (!userPhenotypes.isEmpty()) {
                for (String userPhenotype : userPhenotypes) {
                    result.add(getUserPhenotype(userPhenotype, summarized));
                }
            }
            if (!systemPhenotypes.isEmpty()) {
                result.addAll(getSystemPhenotypes(systemPhenotypes, summarized));
            }
        }
        return result;
    }

    public List<Phenotype> getUserPhenotypes(boolean summarized) throws ClientException {
        final String path = "/api/protected/phenotypes";
        if (summarized) {
            MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
            queryParams.add("summarize", "true");
            return doGet(path, PhenotypeList, queryParams);
        } else {
            return doGet(path, PhenotypeList);
        }
    }

    public Phenotype getUserPhenotype(String inKey, boolean summarized) throws ClientException {
        if (inKey == null) {
            throw new IllegalArgumentException("inKey cannot be null");
        }
        /*
		 * The inKey parameter may contain spaces, slashes and other 
		 * characters that are not allowed in URLs, so it needs to be
		 * encoded. We use UriBuilder to guarantee a valid URL. The inKey
		 * string can't be templated because the slashes won't be encoded!
         */
        String path = UriBuilder
                .fromPath("/api/protected/phenotypes/")
                .segment(inKey)
                .build().toString();

        if (summarized) {
            MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
            queryParams.add("summarize", "true");
            return doGet(path, Phenotype.class, queryParams);
        } else {
            return doGet(path, Phenotype.class);
        }
    }

    public URI saveUserPhenotype(Phenotype inPhenotype) throws ClientException {
        final String path = "/api/protected/phenotypes";
        URI phenotypeURI = doPostCreate(path, inPhenotype);
        return phenotypeURI;
    }

    public void updateUserPhenotype(Long inId, Phenotype inPhenotype) throws
            ClientException {
        if (inId == null) {
            throw new IllegalArgumentException("inId cannot be null");
        }
        final String path = "/api/protected/phenotypes/" + inId;
        doPut(path, inPhenotype);
    }

    public void deleteUserPhenotype(Long inUserId, Long inId) throws
            ClientException {
        if (inUserId == null) {
            throw new IllegalArgumentException("inUserId cannot be null");
        }
        if (inId == null) {
            throw new IllegalArgumentException("inId cannot be null");
        }
        /*
		 * The inKey parameter may contain spaces, slashes and other 
		 * characters that are not allowed in URLs, so it needs to be
		 * encoded. We use UriBuilder to guarantee a valid URL. The inKey
		 * string can't be templated because the slashes won't be encoded!
         */
        final String path = "/api/protected/phenotypes/" + inId;
        doDelete(path);
    }

    public List<SystemPhenotype> getSystemPhenotypes() throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/concepts/").build().toString();
        return doGet(path, SystemPhenotypeList);
    }

    public List<SystemPhenotype> getSystemPhenotypes(List<String> inKeys, boolean summarize) throws ClientException {
        if (inKeys == null) {
            throw new IllegalArgumentException("inKeys cannot be null");
        }
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        for (String key : inKeys) {
            formParams.add("key", key);
        }
        formParams.add("summarize", Boolean.toString(summarize));
        String path = UriBuilder.fromPath("/api/protected/concepts/")
                .build().toString();
        return doPost(path, formParams, SystemPhenotypeList);
    }

    public SystemPhenotype getSystemPhenotype(String inKey, boolean summarize) throws ClientException {
        List<SystemPhenotype> result = getSystemPhenotypes(Collections.singletonList(inKey), summarize);
        if (result.isEmpty()) {
            throw new HttpStatusException(Response.Status.NOT_FOUND);
        } else {
            return result.get(0);
        }
    }

    /*public List<TimeUnit> getTimeUnitsAsc() throws ClientException {
        final String path = "/api/protected/timeunits";
        return doGet(path, TimeUnitList);
    }

    public TimeUnit getTimeUnit(Long inId) throws ClientException {
        final String path = "/api/protected/timeunits/" + inId;
        return doGet(path, TimeUnit.class);
    }

    public TimeUnit getTimeUnitByName(String inName) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/timeunits/byname/")
                .segment(inName)
                .build().toString();
        return doGet(path, TimeUnit.class);
    }

    public List<RelationOperator> getRelationOperatorsAsc() throws ClientException {
        final String path = "/api/protected/relationops";
        return doGet(path, RelationOperatorList);
    }

    public RelationOperator getRelationOperator(Long inId) throws ClientException {
        final String path = "/api/protected/relationops/" + inId;
        return doGet(path, RelationOperator.class);
    }

    public RelationOperator getRelationOperatorByName(String inName) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/relationops/byname/")
                .segment(inName)
                .build().toString();
        return doGet(path, RelationOperator.class);
    }

    public OAuthProvider getOAuthProvider(Long inId) throws ClientException {
        final String path = "/api/protected/oauthproviders/" + inId;
        return doGet(path, OAuthProvider.class);
    }

    public OAuthProvider getOAuthProviderByName(String inName) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/oauthproviders/byname/")
                .segment(inName)
                .build().toString();
        return doGet(path, OAuthProvider.class);
    }

    public List<ThresholdsOperator> getThresholdsOperators() throws ClientException {
        final String path = "/api/protected/thresholdsops/";
        return doGet(path, ThresholdsOperatorList);
    }

    public ThresholdsOperator getThresholdsOperator(Long inId) throws ClientException {
        final String path = "/api/protected/thresholdsops/" + inId;
        return doGet(path, ThresholdsOperator.class);
    }

    public ThresholdsOperator getThresholdsOperatorByName(
            String inName) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/thresholdsops/byname/")
                .segment(inName)
                .build().toString();
        return doGet(path, ThresholdsOperator.class);
    }

    public List<ValueComparator> getValueComparatorsAsc() throws ClientException {
        final String path = "/api/protected/valuecomps";
        return doGet(path, ValueComparatorList);
    }

    public ValueComparator getValueComparator(Long inId) throws ClientException {
        final String path = "/api/protected/valuecomps/" + inId;
        return doGet(path, ValueComparator.class);
    }

    public ValueComparator getValueComparatorByName(String inName) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/valuecomps/byname/")
                .segment(inName)
                .build().toString();
        return doGet(path, ValueComparator.class);
    }

    public List<FrequencyType> getFrequencyTypesAsc() throws ClientException {
        final String path = "/api/protected/frequencytypes";
        return doGet(path, FrequencyTypeList);
    }*/
    public List<SourceConfig> getSourceConfigs() throws ClientException {
        String path = "/api/protected/sourceconfigs";
        return doGet(path, SourceConfigList);
    }

    public SourceConfig getSourceConfig(String sourceConfigId) throws ClientException {
        String path = UriBuilder.fromPath("/api/protected/sourceconfigs/")
                .segment(sourceConfigId)
                .build().toString();
        return doGet(path, SourceConfig.class);
    }

    public List<SourceConfigParams> getSourceConfigParams() throws ClientException {
        String path = "/api/protected/sourceconfigs/parameters/list";
        return doGet(path, SourceConfigParamsList);
    }

    public Long createDestination(Destination destination) throws ClientException {
        String path = "/api/protected/destinations";
        URI destURI = doPostCreate(path, destination);
        return extractId(destURI);
    }

    public void updateDestination(Destination destination) throws ClientException {
        String path = "/api/protected/destinations";
        doPut(path, destination);
    }

    public List<Destination> getDestinations() throws ClientException {
        String path = "/api/protected/destinations";
        return doGet(path, DestinationList);
    }

    public List<CohortDestination> getCohortDestinations() throws
            ClientException {
        final String path = "/api/protected/destinations/";
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", DestinationType.COHORT.name());
        return doGet(path, CohortDestinationListType, queryParams);
    }

    public List<I2B2Destination> getI2B2Destinations() throws
            ClientException {
        final String path = "/api/protected/destinations/";
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", DestinationType.I2B2.name());
        return doGet(path, I2B2DestinationListType, queryParams);
    }

    public Destination getDestination(String destinationId) throws ClientException {
        String path = UriBuilder.fromPath("/api/protected/destinations/")
                .segment(destinationId)
                .build().toString();
        return doGet(path, Destination.class);
    }

    public void deleteDestination(Long id, String destinationId) throws ClientException {
        String path = UriBuilder.fromPath("/api/protected/destinations/")
                .segment(destinationId)
                .build().toString();
        doDelete(path);
    }

    //Search Functionality
    public List<String> getSystemPhenotypeSearchResults(String searchKey) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/concepts/search/")
                .segment(searchKey)
                .build().toString();
        return doGet(path, SystemPhenotypeSearchResultsList);
    }

    //Search Functionality
    public List<SystemPhenotype> getSystemPhenotypeSearchResultsBySearchKey(String searchKey) throws ClientException {
        final String path = UriBuilder.fromPath("/api/protected/concepts/propsearch/")
                .segment(searchKey)
                .build().toString();
        return doGet(path, SystemPhenotypeList);
    }

    public InputStream getOutput(String destinationId) throws ClientException {
        String path = UriBuilder.fromPath("/api/protected/output/")
                .segment(destinationId)
                .build().toString();
        return doGet(path, InputStream.class);
    }

}
