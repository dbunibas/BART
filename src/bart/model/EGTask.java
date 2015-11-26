package bart.model;

import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.MainMemoryDB;
import bart.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;

public class EGTask {

    private String name;
    private String absolutePath;
    private IDatabase source;
    private IDatabase target;
    private IDatabase dirtyTarget;
    private List<Dependency> dcs = new ArrayList<Dependency>();
    private List<String> authoritativeSources = new ArrayList<String>();
    private EGTaskConfiguration configuration = new EGTaskConfiguration();

    public EGTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public IDatabase getSource() {
        return source;
    }

    public void setSource(IDatabase source) {
        this.source = source;
    }

    public IDatabase getTarget() {
        return target;
    }

    public void setTarget(IDatabase target) {
        this.target = target;
    }

    public IDatabase getDirtyTarget() {
        return dirtyTarget;
    }

    public void setDirtyTarget(IDatabase dirtyTarget) {
        this.dirtyTarget = dirtyTarget;
    }

    public List<Dependency> getDCs() {
        return dcs;
    }

    public void setDCs(List<Dependency> dcs) {
        this.dcs = dcs;
    }

    public Dependency getDependency(String dependencyId) {
        for (Dependency dependency : dcs) {
            if (dependency.getId().equals(dependencyId)) {
                return dependency;
            }
        }
        throw new IllegalArgumentException("Dependency id " + dependencyId + " doesn't exist");
    }

    public List<String> getAuthoritativeSources() {
        return authoritativeSources;
    }

    public void addAuthoritativeSource(String authoritativeSource) {
        this.authoritativeSources.add(authoritativeSource);
    }

    public void setAuthoritativeSources(List<String> authoritativeSources) {
        this.authoritativeSources = authoritativeSources;
    }

    public boolean isMainMemory() {
        return (this.source == null || this.source instanceof MainMemoryDB || this.source instanceof EmptyDB)
                && (this.target instanceof MainMemoryDB || this.target instanceof EmptyDB);
    }

    public boolean isDBMS() {
        return (this.source == null || this.source instanceof EmptyDB || this.source instanceof DBMSDB)
                && (this.target instanceof DBMSDB);
    }

    public EGTaskConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(EGTaskConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("=============================== EG TASK ================================\n");
        result.append("Config: \n").append(configuration).append("\n");
        if (isMainMemory()) {
            result.append("Source:\n").append(this.source).append("\n");
            result.append("Target:\n").append(this.target).append("\n");
        } else {
            result.append("Source:\n").append(this.source.printSchema()).append("\n");
            result.append("Target:\n").append(this.target.printSchema()).append("\n");
        }
        if (!this.authoritativeSources.isEmpty()) {
            result.append("Authoritative sources: ").append(this.authoritativeSources).append("\n");
        }
        if (!this.dcs.isEmpty()) {
            result.append("================ Denial Constraints ===================\n");
            for (Dependency dtgd : this.dcs) {
                result.append(dtgd).append("\n");
            }
        }
        return result.toString();
    }

}
