package hotelreservation.exceptions;

import hotelreservation.RestExceptionHandler;
import hotelreservation.controller.ReservationController;
import hotelreservation.model.ui.GuestDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class RestExceptionHandlerTest {

    private MockMvc mvc;

    @Autowired
    private ReservationController reservationController;

    @Before
    public void setup() {
        this.mvc = standaloneSetup(reservationController).setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
    }

    @Test
    @WithUserDetails("admin")
    public void testHandleBindExceptionHandler() throws Exception {
        GuestDTO guestDTO = new GuestDTO("", null, "ignored", null, null);

        mvc.perform(post("/addOccupant/1").flashAttr("guestDTO", guestDTO)).andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("[guestDTO.firstName,firstName]; arguments []; default message [firstName]]; default message [must not be blank]")))
                .andExpect(content().string(containsString("[guestDTO.lastName,lastName]; arguments []; default message [lastName]]; default message [must not be null]")))
                .andExpect(content().string(containsString("[guestDTO.lastName,lastName]; arguments []; default message [lastName]]; default message [must not be blank]")))
                .andExpect(content().string(containsString("[guestDTO.identification,identification]; arguments []; default message [identification]]; default message [must not be null]")))
                .andExpect(content().string(containsString("[guestDTO.firstName,firstName]; arguments []; default message [firstName]]; default message [must not be blank]")));
    }

    @Test
    @WithMockUser(username = "blah", roles = "MISSING_ROLE")
    public void testAccesDeniedExceptionHandler() throws Exception {
        mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
    }


}
