package pl.vemu.zsme.detailedNews;

import android.text.Spanned;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Detail {
    Spanned text;
    List<String> images;
}
