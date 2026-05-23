<template>
  <el-container class="layout-container">
    <Sidebar />
    <el-container>
      <el-header height="50px">
        <Navbar />
      </el-header>
      <TagsView />
      <el-main>
        <AppMain />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import Sidebar from './Sidebar/index'
import Navbar from './Navbar'
import TagsView from './TagsView/index'
import AppMain from './AppMain'

export default {
  components: { Sidebar, Navbar, TagsView, AppMain }
}
</script>
