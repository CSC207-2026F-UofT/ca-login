package use_case.logout;

/**
 * The Logout Interactor.
 */
public class LogoutInteractor implements LogoutInputBoundary {
    private LogoutUserDataAccessInterface userDataAccessObject;
    private LogoutOutputBoundary logoutPresenter;

    public LogoutInteractor(LogoutUserDataAccessInterface userDataAccessInterface,
                            LogoutOutputBoundary logoutOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.logoutPresenter = logoutOutputBoundary;
    }

    @Override
    public void execute() {
        final LogoutOutputData lod = new LogoutOutputData(this.userDataAccessObject.getCurrentUsername());
        this.userDataAccessObject.setCurrentUsername(null);
        this.logoutPresenter.prepareSuccessView(lod);
    }
}

