package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;


public class RedmineCategoryModel implements IMasterModel<RedmineProjectCategory> {
	protected Dao<RedmineProjectCategory, Long> dao;
	public RedmineCategoryModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineProjectCategory.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineProjectCategory> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineProjectCategory> fetchAll(int connection) throws SQLException{
		List<RedmineProjectCategory> item;
		item = dao.queryForEq(RedmineProjectCategory.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineProjectCategory>();
		}
		return item;
	}

	public RedmineProjectCategory fetchById(int connection, int statusId) throws SQLException{
		PreparedQuery<RedmineProjectCategory> query = dao.queryBuilder().where()
		.eq(RedmineProjectCategory.CONNECTION, connection)
		.and()
		.eq(RedmineProjectCategory.CATEGORY_ID, statusId)
		.prepare();
		RedmineProjectCategory item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineProjectCategory();
		return item;
	}

	public RedmineProjectCategory fetchById(long id) throws SQLException{
		RedmineProjectCategory item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineProjectCategory();
		return item;
	}

	public int insert(RedmineProjectCategory item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineProjectCategory item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineProjectCategory item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(long id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}

	public void refreshItem(RedmineIssue data) throws SQLException{
		RedmineProjectCategory item = refreshItem(data.getConnectionId(),data.getCategory());
		data.setCategory(item);
	}
	public RedmineProjectCategory refreshItem(RedmineConnection info,RedmineProjectCategory data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineProjectCategory refreshItem(int connection_id,RedmineProjectCategory data) throws SQLException{
		if(data == null)
			return null;

		RedmineProjectCategory project = this.fetchById(connection_id, data.getCategoryId());
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			data = fetchById(connection_id, data.getCategoryId());
		} else {
			data.setId(project.getId());

			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(!project.getModified().before(data.getModified())){
				this.update(data);
			}
		}
		return data;
	}
	@Override
	public long countByProject(int connection_id, long project_id) throws SQLException {
		QueryBuilder<RedmineProjectCategory, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineProjectCategory.CONNECTION, connection_id)
				.and()
				.eq(RedmineProjectCategory.PROJECT_ID, project_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineProjectCategory fetchItemByProject(int connection_id,
			long project_id, long offset, long limit) throws SQLException {
		QueryBuilder<RedmineProjectCategory, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineProjectCategory.NAME, true)
			.where()
				.eq(RedmineProjectCategory.CONNECTION, connection_id)
				.and()
				.eq(RedmineProjectCategory.PROJECT_ID, project_id)
				;
		RedmineProjectCategory item = builder.queryForFirst();
		if(item == null)
			item = new RedmineProjectCategory();
		return item;
	}
}
