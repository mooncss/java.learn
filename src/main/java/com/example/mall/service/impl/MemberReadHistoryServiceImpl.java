package com.example.mall.service.impl;

//import com.example.mall.portal.repository.MemberReadHistoryRepository;

/**
 * 会员浏览记录管理Service实现类
 * Created by macro on 2018/8/3.
 */
//@Service
//public class MemberReadHistoryServiceImpl implements MemberReadHistoryService {
//    @Autowired
//    private MemberReadHistoryRepository memberReadHistoryRepository;
//    @Override
//    public int create(MemberReadHistory memberReadHistory) {
//        memberReadHistory.setId(null);
//        memberReadHistory.setCreateTime(new Date());
//        memberReadHistoryRepository.save(memberReadHistory);
//        return 1;
//    }
//
//    @Override
//    public int delete(List<String> ids) {
//        List<MemberReadHistory> deleteList = new ArrayList<>();
//        for(String id:ids){
//            MemberReadHistory memberReadHistory = new MemberReadHistory();
//            memberReadHistory.setId(id);
//            deleteList.add(memberReadHistory);
//        }
//        memberReadHistoryRepository.deleteAll(deleteList);
//        return ids.size();
//    }
//
//    @Override
//    public List<MemberReadHistory> list(Long memberId) {
//        return memberReadHistoryRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
//    }
//}
