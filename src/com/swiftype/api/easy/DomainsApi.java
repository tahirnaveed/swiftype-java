package com.swiftype.api.easy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.swiftype.api.easy.helper.Client;
import com.swiftype.api.easy.helper.Client.Response;

public class DomainsApi {
	private final String engineId;

	public DomainsApi(final String engineId) {
		this.engineId = engineId;
	}

	/**
	 * @return	List of all domains for an engine
	 */
	public Domain[] getAll() {
		final Response response = Client.get(domainsPath());
		try {
			final JSONArray domainsJson = new JSONArray(response.body);
			final Domain[] domains = new Domain[domainsJson.length()];
			for (int i = 0; i < domains.length; ++i) {
				domains[i] = Domain.fromJson(engineId, domainsJson.getJSONObject(i));
			}
			return domains;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * @param domainId	Id of the wanted domain
	 * @return			Specified domain
	 */
	public Domain get(final String domainId) {
		final Response response = Client.get(domainPath(domainId));
		return toDomain(response);
	}

	/**
	 * @param url	Start URL used for crawling the domain
	 * @return		Domain belonging to the specified URL
	 */
	public Domain create(final String url) {
		final Response response = Client.post(domainsPath(), "{\"domain\": {\"submitted_url\": \"" + url + "\"} }");
		return toDomain(response);
	}

	/**
	 * @param domainId	Id of the domain
	 * @return			Success of deletion
	 */
	public boolean destroy(final String domainId) {
		return Client.delete(domainPath(domainId)).isSuccess();
	}

	/**
	 * @param domainId	Id of the domain
	 * @return			Asynchronously recrawled domain
	 */
	public Domain recrawl(final String domainId) {
		final Response response = Client.put(domainPath(domainId) + "/recrawl", "");
		return toDomain(response);
	}

	/**
	 * @param domainId	Id of the domain
	 * @param url		URL to add or update on this domain
	 * @return
	 */
	public boolean crawlUrl(final String domainId, final String url) {
		return Client.put(domainPath(domainId) + "/crawl_url", "{\"url\": \"" + url + "\"}").isSuccess();
	}

	String domainsPath() {
		return EnginesApi.enginePath(engineId) + "/domains";
	}

	String domainPath(final String domainId) {
		return domainsPath() + "/" + domainId;
	}

	private Domain toDomain(final Response response){
		try {
			final JSONObject json = new JSONObject(response.body);
			return Domain.fromJson(engineId, json);
		} catch (JSONException e) {
			return null;
		}
	}
}
