package ai.tuliprose.libs.quintean;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class QuinteanRepository {
    public static class Builder {
        private Map<QuinteanType, Quintean> typesAndQuinteans;
        private List<Quintean> quinteans;
        
		public Builder withQuinteans(List<Quintean> quinteans) {
			this.quinteans = quinteans;
			return this;
		}

        public Builder withTypesAndQuinteans(Map<QuinteanType, Quintean> typesAndQuinteans) {
            this.typesAndQuinteans = typesAndQuinteans;
            return this;
        }

        public QuinteanRepository build() {
            QuinteanRepository repository = new QuinteanRepository();
            
            if (typesAndQuinteans != null) {
                for (Map.Entry<QuinteanType, Quintean> entry : typesAndQuinteans.entrySet()) {
                    QuinteanType type = entry.getKey();
                    Quintean quintean = entry.getValue();
                    repository.addQuinteanNode(new QuinteanNode(type, quintean));
                }
            } else if (quinteans != null) {
            	for (Quintean quintean : quinteans) {
            		repository.addQuinteanNode(quintean);
            	}
            } else {
                for (Map.Entry<String, QuinteanType> entry : DEFAULT_QUINTEAN_TYPES.entrySet()) {
                    QuinteanType type = entry.getValue();
                    repository.addQuinteanNode(new QuinteanNode(type, Quintean.getRandom()));
                }
            }
            
            resetBuilder();
            return repository;
        }
        
        private void resetBuilder() {
        	typesAndQuinteans = null;
        	quinteans = null;
        }
    }

    private static final Locale defaultLocale = Locale.DE;

    public static final Map<String, QuinteanType> DEFAULT_QUINTEAN_TYPES = Map.of(
            "Freude", new QuinteanType("Freude", "Gefühl", defaultLocale),
            "Trauer", new QuinteanType("Trauer", "Gefühl", defaultLocale),
            "Wut", new QuinteanType("Wut", "Gefühl", defaultLocale),
            "Angst", new QuinteanType("Angst", "Gefühl", defaultLocale),
            "Überraschung", new QuinteanType("Überraschung", "Gefühl", defaultLocale),
            "Energie", new QuinteanType("Energie", "Körper", defaultLocale),
            "Schmerz", new QuinteanType("Schmerz", "Körper", defaultLocale),
            "Hunger", new QuinteanType("Hunger", "Körper", defaultLocale),
            "Durst", new QuinteanType("Durst", "Körper", defaultLocale),
            "Atem", new QuinteanType("Atem", "Körper", defaultLocale)
    );

    private NavigableSet<QuinteanNode> quinteanNodes = new ConcurrentSkipListSet<>();
    private int usageIteration = 0;
    
    public static Builder builder() {
        return new Builder();
    }

    public QuinteanNode getQuinteanNodeByUUID(UUID uuid) {
        for (QuinteanNode node : quinteanNodes) {
            if (node.getId().equals(uuid)) {
                return node;
            }
        }
        return null;
    }

    public QuinteanNode addQuinteanNode(Quintean quintean) {
		QuinteanNode node = new QuinteanNode(quintean);
		addQuinteanNode(node);
		return node;
	}

	public QuinteanNode addQuinteanNode(QuinteanType type, Quintean value) {
        QuinteanNode node = new QuinteanNode(type, value);
        addQuinteanNode(node);
        return node;
    }

    public QuinteanNode addQuinteanNode(QuinteanType type) {
        QuinteanNode node = new QuinteanNode(type, Quintean.getRandom());
        addQuinteanNode(node);
        return node;
    }

	public void addQuinteanNode(QuinteanNode node) {
		quinteanNodes.add(node);
		node.afterRegistration(this);
	}

    public List<QuinteanNode> getRootNodes() {
        return quinteanNodes.parallelStream()
                .filter(node -> node.getParents().isEmpty())
                .toList();
    }
    
    public List<QuinteanNode> getLeafNodes() {
    	return quinteanNodes.parallelStream()
    			.filter(node -> node.getChildren().isEmpty())
    			.toList();
    }

	public List<QuinteanNode> getAllNodes() {
		return List.copyOf(quinteanNodes);
	}

	public QuinteanNode getQuinteanNodeByType(QuinteanType type) {
        for (QuinteanNode node : quinteanNodes) {
            if (node.getType().equals(type)) {
                return node;
            }
        }
        return null;
    }

    public QuinteanNode getQuinteanNodeByType(String type) {
        for (QuinteanNode node : quinteanNodes) {
            if (node.getType().getName().equals(type)) {
                return node;
            }
        }
        return null;
    }

    public QuinteanNode getQuinteanNodeByType(String type, String locale) {
        for (QuinteanNode node : quinteanNodes) {
            if (node.getType().getName().equals(type) && node.getType().getLocale().equals(locale)) {
                return node;
            }
        }
        return null;
    }

    public boolean removeQuinteanNode(UUID uuid) {
        QuinteanNode node = getQuinteanNodeByUUID(uuid);
        if (node != null) {
            quinteanNodes.remove(node);
            return true;
        }
        return false;
    }
    
    public boolean removeQuinteanNode(QuinteanNode node) {
        if (quinteanNodes.contains(node)) {
            quinteanNodes.remove(node);
            return true;
        }
        return false;
    }

    public void expand() {
		for (QuinteanNode node : quinteanNodes) {
			node.expand();
		}
	}
	
	public void shrink() {
		for (QuinteanNode node : quinteanNodes) {
			node.shrink();
		}
	}

	public boolean canGrow() {
		return calculateState() < Quintean.MEDIUM.getMaxValue();
	}

	private double calculateState() {
        return quinteanNodes.stream()
            .mapToDouble(QuinteanNode::calculateOutput)
            .average()
            .orElse(50.0);
	}

	public void grow() {
		for (QuinteanNode node : quinteanNodes) {
			node.grow();				
		}
	}

	public void activate() {
		if (usageIteration++ < 1) {
            for (QuinteanNode node : quinteanNodes) {
                if (!node.isActivated()) {
                	node.activate();
                }
            }
                        
			deactivate();			
		} else if (usageIteration % 5 < 3) {
			shrink();
		} else if (canGrow()) {
			grow();
		} else {
			shrink();
		}
	}
	
	public void deactivate() {
        for (QuinteanNode node : quinteanNodes) {
            node.deactivate();
        }
	}
	
	public int size() {
		return quinteanNodes.size();
	}
	
	public String toJson() {
		return toJson(0);
	}
	
	public String toJson(int indent) {
		String json = "{\n" + tabs(indent) + 
				"\"model-repository\": [\n" + tabs(indent + 1);
		
		for (QuinteanNode rootNode : getRootNodes()) {
			json += rootNode.toJson(indent + 2);
		}
		
		return json + "\n" + tabs(indent) + "]\n}";
	}
	
	static String tabs(int count) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < count + 1; i++) {
			builder.append("\t");
		}
		
		return builder.toString();
	}
}
