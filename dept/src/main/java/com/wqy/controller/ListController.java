package com.wqy.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.wqy.domain.Dept;
import com.wqy.service.DeptService;
import com.wqy.service.UserService;
import com.wqy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("dept")
@CrossOrigin
public class ListController {

    @Autowired
    DeptService deptService;
    @Autowired
    UserService userService;

    /**
     * 列表查询
     *
     * @return
     */
    @RequestMapping("list")
    public Result getlist() {
        List<Dept> list = deptService.getlist();
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();

//转换器 (含义:找出父节点为字符串零的所有子节点, 并递归查找对应的子节点, 深度最多为 3)
        List<Tree<String>> build = TreeUtil.build(list, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getDeptId().toString());
                    tree.setParentId(treeNode.getParentId().toString());
                    // 扩展属性 ...
                    tree.putExtra("deptId", treeNode.getDeptId());
                    tree.putExtra("deptName", treeNode.getDeptName());
                    tree.putExtra("leader", treeNode.getLeader());
                    tree.putExtra("phone", treeNode.getPhone());
                    tree.putExtra("email", treeNode.getEmail());
                    tree.putExtra("status", treeNode.getStatus());
                    tree.putExtra("createTime", treeNode.getCreateTime());
                });

        return Result.success(build);
    }

    @RequestMapping("update")
    public Result update(@RequestBody Dept dept) {
        deptService.updateById(dept);
        return Result.success("修改完成");
    }


    @RequestMapping("deletion")
    public Result falseDelete(String deptId) {
        Dept byId = deptService.getById(deptId);
        byId.setStatus("3");
        deptService.updateById(byId);
        return Result.success("删除完成");
    }


}
