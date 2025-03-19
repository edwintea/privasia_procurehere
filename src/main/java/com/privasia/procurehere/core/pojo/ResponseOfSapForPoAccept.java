package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseOfSapForPoAccept {

    @JsonProperty("MT_POAcknowledge_P2P_Res")
    private POAcknowledgeResponse mtPoAcknowledgeP2PRes;

    public POAcknowledgeResponse getMtPoAcknowledgeP2PRes() {
        return mtPoAcknowledgeP2PRes;
    }

    public void setMtPoAcknowledgeP2PRes(POAcknowledgeResponse mtPoAcknowledgeP2PRes) {
        this.mtPoAcknowledgeP2PRes = mtPoAcknowledgeP2PRes;
    }

    @Override
    public String toString() {
        return "ResponseOfSapForPoAccept{" +
                "mtPoAcknowledgeP2PRes=" + mtPoAcknowledgeP2PRes +
                '}';
    }

    public static class POAcknowledgeResponse {

        @JsonProperty("the_description")
        private String theDescription;


        @JsonProperty("type")
        private String type;

        public String getTheDescription() {
            return theDescription;
        }

        public void setTheDescription(String theDescription) {
            this.theDescription = theDescription;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "POAcknowledgeResponse{" +
                    "theDescription='" + theDescription + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
