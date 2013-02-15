package com.reviewer.model;

import com.reviewer.io.Serializable;

public interface Command extends Serializable {
	void apply(System system);
}
