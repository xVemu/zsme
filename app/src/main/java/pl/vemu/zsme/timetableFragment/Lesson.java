package pl.vemu.zsme.timetableFragment;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Lesson {
    private String name, room, teacher, hour, index;
}
