// SPDX-License-Identifier: MIT
package com.mercedesbenz.sechub.restdoc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.mercedesbenz.sechub.test.TestURLBuilder.*;
import static com.mercedesbenz.sechub.test.TestURLBuilder.RestDocPathParameter.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.annotation.Annotation;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.mercedesbenz.sechub.docgen.util.RestDocFactory;
import com.mercedesbenz.sechub.domain.administration.project.ProjectAdministrationRestController;
import com.mercedesbenz.sechub.domain.scan.admin.FullScanData;
import com.mercedesbenz.sechub.domain.scan.admin.FullScanDataRestController;
import com.mercedesbenz.sechub.domain.scan.admin.FullScanDataService;
import com.mercedesbenz.sechub.domain.scan.admin.ScanData;
import com.mercedesbenz.sechub.domain.scan.log.ProjectScanLog;
import com.mercedesbenz.sechub.sharedkernel.Profiles;
import com.mercedesbenz.sechub.sharedkernel.RoleConstants;
import com.mercedesbenz.sechub.sharedkernel.configuration.AbstractAllowSecHubAPISecurityConfiguration;
import com.mercedesbenz.sechub.sharedkernel.logging.AuditLogService;
import com.mercedesbenz.sechub.sharedkernel.logging.LogSanitizer;
import com.mercedesbenz.sechub.sharedkernel.usecases.UseCaseRestDoc;
import com.mercedesbenz.sechub.sharedkernel.usecases.admin.project.UseCaseAdminDownloadsFullScanDataForJob;
import com.mercedesbenz.sechub.test.ExampleConstants;
import com.mercedesbenz.sechub.test.TestPortProvider;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectAdministrationRestController.class)
@ContextConfiguration(classes = { FullScanDataRestController.class, DownloadsFullScanDataForJobRestDocTest.SimpleTestConfiguration.class, LogSanitizer.class })
@WithMockUser(authorities = RoleConstants.ROLE_SUPERADMIN)
@ActiveProfiles(Profiles.TEST)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = ExampleConstants.URI_SECHUB_SERVER, uriPort = 443)
public class DownloadsFullScanDataForJobRestDocTest {

    private static final int PORT_USED = TestPortProvider.DEFAULT_INSTANCE.getRestDocTestPort();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    FullScanDataService fullScanDataService;

    @MockBean
    AuditLogService auditLogService;

    private UUID jobUUID;

    @Before
    public void before() {
        jobUUID = UUID.randomUUID();
        FullScanData data = new FullScanData();
        ScanData d = new ScanData();
        d.productId = "productX";
        d.result = "{ 'result':'OK'}";
        d.metaData = "{}";
        data.allScanData.add(d);

        String config = "{}";
        ProjectScanLog log = new ProjectScanLog("theProject", jobUUID, "spartakus", config);
        data.allScanLogs.add(log);

        when(fullScanDataService.getFullScanData(jobUUID)).thenReturn(data);
    }

    @Test
    @UseCaseRestDoc(useCase = UseCaseAdminDownloadsFullScanDataForJob.class)
    public void restdoc_admin_downloads_fullscan_data_for_job() throws Exception {
        /* prepare */
        String apiEndpoint = https(PORT_USED).buildAdminDownloadsZipFileContainingFullScanDataFor(JOB_UUID.pathElement());
        Class<? extends Annotation> useCase = UseCaseAdminDownloadsFullScanDataForJob.class;

        /* execute + test @formatter:off */
		this.mockMvc.perform(
				get(apiEndpoint,jobUUID).
				contentType(MediaType.APPLICATION_JSON_VALUE)
				).
		andExpect(status().isOk()).
		andDo(document(RestDocFactory.createPath(useCase),
                resource(
                        ResourceSnippetParameters.builder().
                            summary(RestDocFactory.createSummary(useCase)).
                            description(RestDocFactory.createDescription(useCase)).
                            tag(RestDocFactory.extractTag(apiEndpoint)).
                            responseSchema(OpenApiSchema.FULL_SCAN_DATA_ZIP.getSchema()).
                            pathParameters(
                                    parameterWithName(JOB_UUID.paramName()).description("The job UUID")
                            ).
                            build()
                         )
		       ));

		/* @formatter:on */
    }

    @Profile(Profiles.TEST)
    @EnableAutoConfiguration
    public static class SimpleTestConfiguration extends AbstractAllowSecHubAPISecurityConfiguration {

    }

}
