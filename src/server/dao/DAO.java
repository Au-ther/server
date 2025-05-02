package server.dao;

public interface DAO<T,U> {
	/**
	 * 查找所有数据。
	 * @return 该类型(T)的所有数据的Vector对象。
	 * @throws Exception 处理过程中发生错误均抛出异常。
	 */
	/**
	 * 添加该对象。
	 * @param obj 添加对象(T)。
	 * @return 添加成功否。
	 * @throws Exception 处理过程中发生错误均抛出异常。
	 */
	boolean add(T obj)throws Exception;

	/**
	 * 删除该对象。
	 * @param obj 删除的对象(T)。
	 * @return 删除成功否。
	 * @throws Exception 处理过程中错误均抛出异常。
	 */
	boolean delete(T obj)throws Exception;
	/**
	 * 更新该对象
	 * @param obj 更新的对象(T)。 
	 * @return 更新成功否。
	 * @throws Exception 处理过程中发生错均抛出异常。
	 */
	boolean update(T obj)throws Exception;
	/**
	 * 按id查找该对象。
	 * @param id 查找的id。
	 * @return 返回查找的对象(T)。
	 * @throws Exception 处理过程中发生错误均抛出异常。
	 */
	T findById(U id)throws Exception;
}
