package cn.bzeal.schoolblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserModel extends PageModel {

    private String username = "";
    private String password;
    private String name = "";
    private String college = "";
    private boolean byCondition = false;

}
