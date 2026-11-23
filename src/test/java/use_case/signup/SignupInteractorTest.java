package use_case.signup;

import data_access.InMemoryUserDataAccessObject;
import entity.UserFactory;
import entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    // This class acts as our "presenter" for our test.
    // What it does is simply record what data is passed into its method
    // calls, then the test can make assertions using this information.
    // Note: this recorder fixes a limitation of the old test structure
    // where the asserts were only made inside the mock Presenter calls, which
    // meant that an empty implementation of the SignupInteractor's execute method
    // would actually pass the test.
    private static class OutputRecorder implements SignupOutputBoundary {
        SignupOutputData success;
        String failure;

        @Override
        public void prepareSuccessView(SignupOutputData data) {
            success = data;
        }

        @Override
        public void prepareFailView(String error) {
            failure = error;
        }

        @Override
        public void switchToLoginView() {
            /* for our tests, we don't need to record anything
               in this case.
             */
        }
    }

    @Test
    void successTest() {
        SignupInputData inputData = new SignupInputData("Paul", "password", "password");
        SignupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();
        OutputRecorder recorder = new OutputRecorder();

        SignupInputBoundary interactor = new SignupInteractor(userRepository, recorder, new UserFactory());
        interactor.execute(inputData);

        // assertions of correctness using the recorder values
        assertNotNull(recorder.success);
        assertEquals("Paul", recorder.success.getUsername());
        assertNull(recorder.failure);
        // checking that the user was actually stored by our DAO
        assertTrue(userRepository.existsByName("Paul"));
    }

    @Test
    void failurePasswordMismatchTest() {
        SignupInputData inputData = new SignupInputData("Paul", "password", "wrong");
        SignupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();
        OutputRecorder recorder = new OutputRecorder();

        SignupInputBoundary interactor = new SignupInteractor(userRepository, recorder, new UserFactory());
        interactor.execute(inputData);

        // assertions of correctness using the recorder values
        assertNull(recorder.success);
        assertNotNull(recorder.failure);
        assertEquals("Passwords don't match.", recorder.failure);
        assertFalse(userRepository.existsByName("Paul"));
    }

    @Test
    void failureUserExistsTest() {
        SignupInputData inputData = new SignupInputData("Paul", "password", "password");
        SignupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // Add Paul to the repo so that when we check later they already exist
        UserFactory factory = new UserFactory();
        User user = factory.create("Paul", "pwd");
        userRepository.save(user);

        OutputRecorder recorder = new OutputRecorder();

        SignupInputBoundary interactor = new SignupInteractor(userRepository, recorder, factory);
        interactor.execute(inputData);

        // assertions of correctness using the recorder values
        assertNull(recorder.success);
        assertNotNull(recorder.failure); // could just check that a failure was reported or...
        assertEquals("User already exists.", recorder.failure); // could check specific msg
        assertTrue(userRepository.existsByName("Paul"));
    }
}