package pl.vemu.zsme.timetableFragment.lesson;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Lesson {
    String name, room, teacher, timeStart, timeFinish, index;
}
