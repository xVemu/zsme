package pl.vemu.zsme.detailedNews;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DetailItem {
    private String detailText, author, date;
}
