package pl.vemu.zsme.timetableFragment.table;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Table {
    String name, room, teacher, timeStart, timeFinish, index;
}
