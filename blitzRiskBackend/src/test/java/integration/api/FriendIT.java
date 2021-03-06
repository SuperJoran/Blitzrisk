package integration.api;

import be.kdg.model.InvitationStatus;
import be.kdg.model.Player;
import be.kdg.services.GameService;
import be.kdg.services.PlayerService;
import be.kdg.services.UserService;
import integration.MyServerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = {"/testcontext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class FriendIT {
    private final String URL = MyServerConfiguration.getURL() + "api/";


    @Autowired
    public UserService userService;

    @Before
    public void setUp() {
        try {
            userService.addUser("friendtestuser1", "testuserpass", "friendtestuser1@test.be");
            userService.addUser("friendtestuser2", "testuserpass", "friendtestuser2@test.be");
            userService.addUser("friendtestuser3", "testuserpass", "friendtestuser3@test.be");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @After
    public void cleanUp() {


    }


    @Test
    public void testAddAndAcceptFriend() {
        String token = given().header("name", "friendtestuser1").header("password", "testuserpass").get(URL + "login").getBody().asString();
        given().header("X-Auth-Token", token).post(URL + "addFriend/friendtestuser2").then().assertThat().statusCode(200);
        given().header("X-Auth-Token", token).get(URL + "friends").then().assertThat().statusCode(200);

        token = given().header("name", "friendtestuser2").header("password", "testuserpass").get(URL + "login").getBody().asString();
        given().header("X-Auth-Token", token).post(URL + "acceptFriend/friendtestuser1").then().assertThat().statusCode(200);

    }

    @Test
    public void testAddFriendTwice() {
        String token = given().header("name", "friendtestuser1").header("password", "testuserpass").get(URL + "login").getBody().asString();
        given().header("X-Auth-Token", token).post(URL + "addFriend/friendtestuser3").then().assertThat().statusCode(200);
        given().header("X-Auth-Token", token).post(URL + "addFriend/friendtestuser3").then().assertThat().statusCode(400);

    }

    @Test
    public void testAcceptNonExistingFriendRequest() {
        String token = given().header("name", "friendtestuser2").header("password", "testuserpass").get(URL + "login").getBody().asString();
        given().header("X-Auth-Token", token).post(URL + "acceptFriend/friendtestuser9000").then().assertThat().statusCode(400);
    }


}
