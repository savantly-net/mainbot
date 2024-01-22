package net.savantly.mainbot.service.replicate;

import lombok.Data;

@Data
public class ReplicateRequest<I extends ReplicateVersionHolder, O> implements ReplicateVersionHolder {
    
    private I input;

    public ReplicateRequest(I input) {
        this.input = input;
    }

    @Override
    public String getVersion() {
        return input.getVersion();
    }
}
