	package tool.plugin.command.admin;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface CommandAdmin {
	String name();
}