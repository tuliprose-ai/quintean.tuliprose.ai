package ai.tuliprose.libs.quintean;

import static ai.tuliprose.libs.quintean.QuinteanRepository.tabs;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Iterator;

public class QuinteanNode implements Comparable<QuinteanNode> {
    private final List<QuinteanNode> parents = new ArrayList<>();
    private final List<QuinteanNode> children = new ArrayList<>();

    private final QuinteanType type;
    private final UUID id = UUID.randomUUID();

    private Quintean value;

    private QuinteanRepository repository;
	private boolean activated;

    public QuinteanNode(Quintean quintean) {
		this(null, null, quintean);
	}
    
    public QuinteanNode(QuinteanType type, Quintean value) {
        this(null, type, value);
    }

    public QuinteanNode(List<QuinteanNode> parents, QuinteanType type, Quintean value) {
        if (parents != null) {
        	this.parents.addAll(parents);	
        }

        this.type = type;
        this.value = value;
    }

	void afterRegistration(QuinteanRepository repository) {
    	this.repository = repository;
    }
    
    public List<QuinteanNode> getParents() {
        return parents;
    }

    public boolean addParent(QuinteanNode parent) {
        repository.addQuinteanNode(parent);
        return parents.add(parent);
    }

    public boolean removeParent(UUID parentUuid) {
        Iterator<QuinteanNode> iterator = parents.iterator();
        
        while (iterator.hasNext()) {
            QuinteanNode parent = iterator.next();
            if (parent.id.equals(parentUuid)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }
    
    public List<QuinteanNode> getChildren() {
        return children;
    }
    
    public boolean addChild(QuinteanNode child) {
        repository.addQuinteanNode(child);
    	return children.add(child);
    }

    public boolean removeChild(UUID childUuid) {
        Iterator<QuinteanNode> iterator = children.iterator();
        
        while (iterator.hasNext()) {
            QuinteanNode child = iterator.next();
            if (child.id.equals(childUuid)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public Quintean getValue() {
        return value;
    }

    public boolean expand() {
        Quintean newValue = value;

        if (value.shouldExpand()) {
            newValue = value == Quintean.HIGH ? Quintean.VERY_HIGH :
                    value == Quintean.LOW ? Quintean.VERY_LOW : null;
        } else if (value == Quintean.VERY_HIGH) {
            addChild(new QuinteanNode(List.of(this), type, Quintean.HIGH));
        } else if (value == Quintean.VERY_LOW) {
            addChild(new QuinteanNode(List.of(this), type, Quintean.LOW));
        } else if (value == Quintean.MEDIUM) {
            addChild(new QuinteanNode(List.of(this), type, Quintean.HIGH));
            addChild(new QuinteanNode(List.of(this), type, Quintean.LOW));
            return true;
        }

        if (newValue != null) {
            value = newValue;
            return true;
        } else {
            return false;
        }
   }

	public void shrink() {
		if (value.canShrink()) {
			value = value.shrink();
		}
	}
	
	public void grow() {
		if (value.canGrow()) {
			value = value.grow();
		}
	}

	public boolean isActivated() {
		return activated;
	}

    public void activate() {
        if (value != null) {
            value = value.activate();
            activated = true;
            
            if (value != null) {
                addChild(new QuinteanNode(List.of(this), type, value));
            } else {
                repository.removeQuinteanNode(this);
            }
        }
    }

	public void deactivate() {
        activated = false;
	}

	public double calculateOutput() {
        double parentMidValue = parents.stream()
            .mapToDouble(QuinteanNode::calculateOutput)
            .average()
            .orElse(50.0);

        double nodeMidValue = value != null ? value.getMidValue() : 50;

        return (parentMidValue + nodeMidValue) / 2;
	}

    public QuinteanType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }

    public boolean isRoot() {
        return parents.isEmpty();
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isFull() {
        return !isEmpty();
    }
    
    public boolean isValid() {
        return value != null && type != null;
    }

    public String toJson() {
    	return toJson(0);
    }
    
	public String toJson(int indent) {
		String json = "{\n" + tabs(indent + 1) + "\"model\": " +
				"{\n" + tabs(indent + 2) + "\"children\": [";
		
		for (QuinteanNode node : children) {
			json += node.toJson(indent + 3) + ",";
		}
		
		json = json.substring(0, json.length() - 1);
		
		json +=	"],\n" + tabs(indent + 2) + (type != null ? "\"type\": " +
				type.toJson(indent + 3) + ",\n" + tabs(indent + 2) : "") +
				"\"id\": \"" + id + "\",\n" + tabs(indent + 2) + "\"value\": \"" +
				value.name() + "\"\n" + tabs(indent + 1) + "}" +
				"\n" + tabs(indent) + "}";
		
		return json; 
	
	}
	
    @Override
    public String toString() {
        return "QuinteanNode{" +
                "parents=" + (!parents.isEmpty() ? parents.stream().map(
                    parent -> parent.getId()).toList() : "null") +
                ", children=" + (!children.isEmpty() ? children.stream().map(
                    child -> child.getId()).toList() : "null") +
                ", type=" + type +
                ", id=" + id +
                ", value=" + value +
                '}';
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		QuinteanNode other = (QuinteanNode) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int compareTo(QuinteanNode o) {
		return id.compareTo(o.id);
	}
}
