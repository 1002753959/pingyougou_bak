package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ContentServiceImpl implements ContentService {
	
	@Autowired
	private ContentDao contentDao;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {
		contentDao.insertSelective(content);
	}

    /**
     * 更新广告
     * @param content
     */
	@Override
	public void edit(Content content) {
        //根据传过来的对象的id查出数据库中的广告类型外键
        Content mysqlContent = contentDao.selectByPrimaryKey(content.getId());

        /**
        更改Mysql数据库
        之所以把更改Mysql数据库的代码放在最前面是为了解决方法中设计两种数据库时的事务问题
        因为代码异常之后就会执行声明事务中的rollback进行回滚,在最开始更改 Mysql数据库
         如果在更改Mysql数据库的时候出现了异常,代码就会停下来,不会执行删除Redis的操作,
         加上事务的回滚,Mysql也回到了没有修改之前的状态
         精简理解:让能回滚的Mysql的相关操作放在最前面,这样就算Mysql出错了,后面的代码不会执行到也不会有什么影响
                如果放在最后面mysql出现异常了,mysql能回滚,但是Redis还是删了
                如果redis的执行出现了异常,声明式事务也会执行回滚,Mysql还是会回滚
        */
        contentDao.updateByPrimaryKeySelective(content);

        if (content.getCategoryId().equals(mysqlContent.getCategoryId())) {
            //如果何传过来的外键一样,说明没有改变广告分类,
            //删除Redis中当前categoryId的广告结果集
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        } else {
            //如果传过来的外键和查出来的外键不一样,说明改变了广告分类
            //即删除Redis中的categoryId是传过来的结果集,又删除查出来的cateGoryId的结果集
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
            redisTemplate.boundHashOps("content").delete(mysqlContent.getCategoryId());
        }


	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
        return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	/**
	 * 根据广告表的category分类id查出广告结果集
	 * @param categoryId
	 * @return
	 */
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
    	//先从Redis中查
		List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if (contentList != null && contentList.size() > 0) {
			//如果有直接返回
			return contentList;
		}else {
			//如果Redis中没有从数据库中查
			ContentQuery contentQuery = new ContentQuery();
			contentQuery.createCriteria().andCategoryIdEqualTo(categoryId);
			contentList = contentDao.selectByExample(contentQuery);
			//查出之后保存到Redis中一份
			redisTemplate.boundHashOps("content").put(categoryId,contentList );
			//设置过期时间
			redisTemplate.boundHashOps("content").expire(1, TimeUnit.DAYS);
			//返回结果集
			return contentList;
		}
    }

}
