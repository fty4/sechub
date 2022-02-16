// SPDX-License-Identifier: MIT
package com.mercedesbenz.sechub.restdoc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.mercedesbenz.sechub.test.TestURLBuilder.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.annotation.Annotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.mercedesbenz.sechub.docgen.util.RestDocFactory;
import com.mercedesbenz.sechub.server.core.AnonymousCheckAliveRestController;
import com.mercedesbenz.sechub.sharedkernel.Profiles;
import com.mercedesbenz.sechub.sharedkernel.configuration.AbstractAllowSecHubAPISecurityConfiguration;
import com.mercedesbenz.sechub.sharedkernel.usecases.UseCaseRestDoc;
import com.mercedesbenz.sechub.sharedkernel.usecases.anonymous.UseCaseAnonymousCheckAlive;
import com.mercedesbenz.sechub.test.ExampleConstants;
import com.mercedesbenz.sechub.test.TestPortProvider;

@RunWith(SpringRunner.class)
@WebMvcTest(AnonymousCheckAliveRestController.class)
@ContextConfiguration(classes = { AnonymousCheckAliveRestController.class, AnonymousCheckAliveRestDocTest.SimpleTestConfiguration.class })
@WithMockUser
@ActiveProfiles(Profiles.TEST)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = ExampleConstants.URI_SECHUB_SERVER, uriPort = 443)
public class AnonymousCheckAliveRestDocTest {
    private static final int PORT_USED = TestPortProvider.DEFAULT_INSTANCE.getRestDocTestPort();

    @Autowired
    private MockMvc mockMvc;

    @Test
    @UseCaseRestDoc(useCase = UseCaseAnonymousCheckAlive.class, variant = "HEAD")
    public void calling_check_alive_head_returns_HTTP_200() throws Exception {
        /* prepare */
        String apiEndpoint = https(PORT_USED).buildCheckIsAliveUrl();
        Class<? extends Annotation> useCase = UseCaseAnonymousCheckAlive.class;

        /* execute */
        /* @formatter:off */
        this.mockMvc.perform(
        			head(apiEndpoint)
        		).
        andExpect(status().isOk()).
        andDo(document(RestDocFactory.createPath(useCase, "HEAD"),
                resource(
                        ResourceSnippetParameters.builder().
                            summary(RestDocFactory.createSummary(useCase)).
                            description(RestDocFactory.createDescription(useCase)).
                            tag(RestDocFactory.extractTag(apiEndpoint)).
                            build()
                         )
                ));

        /* @formatter:on */
    }

    @Test
    @UseCaseRestDoc(useCase = UseCaseAnonymousCheckAlive.class, variant = "GET")
    public void calling_check_alive_get_returns_HTTP_200() throws Exception {
        /* prepare */
        String apiEndpoint = https(PORT_USED).buildCheckIsAliveUrl();
        Class<? extends Annotation> useCase = UseCaseAnonymousCheckAlive.class;

        /* execute */
        /* @formatter:off */
        this.mockMvc.perform(
        			get(apiEndpoint)
        		).
        andExpect(status().isOk()).
        andDo(document(RestDocFactory.createPath(useCase, "GET"),
                resource(
                        ResourceSnippetParameters.builder().
                            summary(RestDocFactory.createSummary(useCase)).
                            description(RestDocFactory.createDescription(useCase)).
                            tag(RestDocFactory.extractTag(apiEndpoint)).
                            build()
                         )
                ));

        /* @formatter:on */
    }

    @TestConfiguration
    @Profile(Profiles.TEST)
    @EnableAutoConfiguration
    public static class SimpleTestConfiguration extends AbstractAllowSecHubAPISecurityConfiguration {

    }
}
