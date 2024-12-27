package ModelClass;

public class ImgBBResponse {
    private ImgBBData data;

    public ImgBBData getData() {
        return data;
    }

    public static class ImgBBData {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}