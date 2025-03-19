package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Shubham
 */
public class BudgetPermissions implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean viewer;
	private boolean editor;
	private boolean owner;
	private boolean creator;
	private boolean modifier;
	private boolean disabled;
	private boolean changeBudget;

	public boolean isModifier() {
		return modifier;
	}

	public void setModifier(boolean modifier) {
		this.modifier = modifier;
	}

	public boolean isViewer() {
		return viewer;
	}

	public void setViewer(boolean viewer) {
		this.viewer = viewer;
	}

	public boolean isEditor() {
		return editor;
	}

	public void setEditor(boolean editor) {
		this.editor = editor;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isCreator() {
		return creator;
	}

	public void setCreator(boolean creator) {
		this.creator = creator;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isChangeBudget() {
		return changeBudget;
	}

	public void setChangeBudget(boolean changeBudget) {
		this.changeBudget = changeBudget;
	}
}
