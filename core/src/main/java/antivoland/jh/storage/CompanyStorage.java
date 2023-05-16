package antivoland.jh.storage;

import antivoland.jh.model.Company;

public class CompanyStorage {
    private final JsonFileStorage<Company, String> storage;

    public CompanyStorage() {
        storage = new JsonFileStorage<>("companies", Company.class, Company::getId);
    }

    public Company get(String id) {
        return storage.load(id);
    }

    public void update(Company company) {
        storage.save(company.merge(get(company.getId())));
    }
}