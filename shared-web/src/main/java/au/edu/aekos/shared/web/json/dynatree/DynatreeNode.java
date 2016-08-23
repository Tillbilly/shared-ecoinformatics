package au.edu.aekos.shared.web.json.dynatree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object to build json arrays for dynatree controlled vocab checkbox selectors
 * 
 * For dynatree version 1.2.4
 * 
 * @author btill
 */
public class DynatreeNode implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title; // (required) Displayed name of the node (html is allowed here)
    private String key;// null, // May be used with activate(), select(), find(), ...
    private Boolean isFolder = null; // false // Use a folder icon. Also the node is expandable but not selectable.
    private Boolean isLazy = null;//false // Call onLazyRead(), when the node is expanded for the first time to allow for delayed creation of children.
    private String tooltip; // Show this popup text.
    private String href = null; // Added to the generated <a> tag.
    private String icon = null; // Use a custom image (filename relative to tree.options.imagePath). 'null' for default icon, 'false' for no icon.
    private String addClass = null; // Class name added to the node's span tag.
    private Boolean noLink = null;//false; // Use <span> instead of <a> tag for this node
    private Boolean activate = null;//false, // Initial active status.
    private Boolean focus = null;//false, // Initial focused status.
    private Boolean expand = null;//false, // Initial expanded status.
    private Boolean select = null;//false, // Initial selected status.
    private Boolean hideCheckbox = null;//false, // Suppress checkbox display for this node.
    private Boolean unselectable = null;//false, // Prevent selection.
    // The following attributes are only valid if passed to some functions:
	private List<DynatreeNode> children = new ArrayList<DynatreeNode>();    // Array of child nodes.
	
	public DynatreeNode() {
		super();
	}
	
	public DynatreeNode(String title) {
		super();
		this.title = title;
	}
	
	public DynatreeNode(String value, String displayValue) {
		super();
		this.title = displayValue;
		this.key = value;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}
	public Boolean getIsLazy() {
		return isLazy;
	}
	public void setIsLazy(Boolean isLazy) {
		this.isLazy = isLazy;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getAddClass() {
		return addClass;
	}
	public void setAddClass(String addClass) {
		this.addClass = addClass;
	}
	public Boolean getNoLink() {
		return noLink;
	}
	public void setNoLink(Boolean noLink) {
		this.noLink = noLink;
	}
	public Boolean getActivate() {
		return activate;
	}
	public void setActivate(Boolean activate) {
		this.activate = activate;
	}
	public Boolean getFocus() {
		return focus;
	}
	public void setFocus(Boolean focus) {
		this.focus = focus;
	}
	public Boolean getExpand() {
		return expand;
	}
	public void setExpand(Boolean expand) {
		this.expand = expand;
	}
	public Boolean getSelect() {
		return select;
	}
	public void setSelect(Boolean select) {
		this.select = select;
	}
	public Boolean getHideCheckbox() {
		return hideCheckbox;
	}
	public void setHideCheckbox(Boolean hideCheckbox) {
		this.hideCheckbox = hideCheckbox;
	}
	public Boolean getUnselectable() {
		return unselectable;
	}
	public void setUnselectable(Boolean unselectable) {
		this.unselectable = unselectable;
	}
	public List<DynatreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<DynatreeNode> children) {
		this.children = children;
	}
	
	public void addChild(DynatreeNode node){
		getChildren().add(node);
	}
	
	public void checkTitles(List<String> values){
		if(values.contains(getTitle())){
			this.setSelect(true);
		}
		if(this.getChildren() != null && this.getChildren().size() > 0){
			for(DynatreeNode child : getChildren()){
				child.checkTitles(values);
			}
		}
	}
	
	//Returns true if the parent was expanded, so the outer recursive call can also expand
	public boolean checkTitlesAndExpand(List<String> values){
		boolean expandParent = false;
		if(values.contains(getTitle())){
			this.setSelect(true);
			//this.setExpand(true);
			expandParent = true;
		}
		if(this.getChildren() != null && this.getChildren().size() > 0){
			for(DynatreeNode child : getChildren()){
				if(child.checkTitlesAndExpand(values)){
					this.setExpand(true);
					expandParent = true;
				}
			}
		}
		return expandParent;
	}
	
	public boolean checkKeysAndExpand(List<String> values){
		boolean expandParent = false;
		if(values.contains(getKey())){
			this.setSelect(true);
			//this.setExpand(true);
			expandParent = true;
		}
		if(this.getChildren() != null && this.getChildren().size() > 0){
			for(DynatreeNode child : getChildren()){
				if(child.checkKeysAndExpand(values)){
					this.setExpand(true);
					expandParent = true;
				}
			}
		}
		return expandParent;
	}
	
	
	public void makeNonLeafNodesUnselectable(){
		if(this.getChildren() != null && this.getChildren().size() > 0){
			this.setUnselectable(true);
			for(DynatreeNode child : getChildren()){
				child.makeNonLeafNodesUnselectable();
			}
		}
	}
	
	public DynatreeNode clone(){
		DynatreeNode clone = new DynatreeNode();
		clone.setTitle(title); 
		clone.setKey(key);
        clone.setIsFolder(isFolder);
        clone.setIsLazy(isLazy);
        clone.setTooltip(tooltip);
        clone.setHref(href);
        clone.setIcon(icon);
        clone.setAddClass(addClass);
        clone.setNoLink(noLink);        
        clone.setActivate(activate);
        clone.setFocus(focus);
        clone.setExpand(expand);
        clone.setSelect(select);
        clone.setHideCheckbox(hideCheckbox);
        clone.setUnselectable(unselectable);
		if(children.size() > 0){
			for(DynatreeNode child : children){
				clone.getChildren().add(child.clone());
			}
		}
		return clone;
	}
	
}
