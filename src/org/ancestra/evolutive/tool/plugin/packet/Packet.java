package org.ancestra.evolutive.tool.plugin.packet;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Packet {
	String value();
}