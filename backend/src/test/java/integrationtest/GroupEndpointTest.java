package integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GroupEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

//    @BeforeEach
//    public void beforeEach() {
//        groupRepository.deleteAll();
//        message = Message.MessageBuilder.aMessage()
//            .withTitle(TEST_NEWS_TITLE)
//            .withSummary(TEST_NEWS_SUMMARY)
//            .withText(TEST_NEWS_TEXT)
//            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
//            .build();
//    }

//    @Test
//    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
//        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI)
//                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
//            .andDo(print())
//            .andReturn();
//        MockHttpServletResponse response = mvcResult.getResponse();
//
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
//        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
//
//        List<SimpleMessageDto> simpleMessageDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
//            SimpleMessageDto[].class));
//
//        assertEquals(0, simpleMessageDtos.size());
//    }

    private boolean isNow(LocalDateTime date) {
        LocalDateTime today = LocalDateTime.now();
        return date.getYear() == today.getYear() && date.getDayOfYear() == today.getDayOfYear() &&
            date.getHour() == today.getHour();
    }

}
