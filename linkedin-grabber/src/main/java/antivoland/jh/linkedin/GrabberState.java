package antivoland.jh.linkedin;

@Deprecated
interface GrabberState {

    void run(Grabber grabber, String... params);

    GrabberState OPEN = (grabber, params) -> {

    };
    GrabberState GRAB = (grabber, params) -> {

    };
    GrabberState SCROLL = (grabber, params) -> {

    };
    GrabberState DONE = (grabber, params) -> {
        // do nothing
    };
}