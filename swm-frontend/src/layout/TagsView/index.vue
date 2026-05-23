<template>
  <div class="tags-view">
    <div class="tags-view-wrapper">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :to="tag.path"
        class="tags-view-item"
        :class="{ active: isActive(tag) }"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        {{ tag.meta.title }}
        <span
          v-if="!isAffix(tag)"
          class="tags-view-item-close"
          @click.prevent.stop="closeTag(tag)"
        >
          <i class="el-icon-close"></i>
        </span>
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TagsView',
  data() {
    return {
      visitedViews: []
    }
  },
  watch: {
    $route() {
      this.addView()
    }
  },
  mounted() {
    this.addView()
  },
  methods: {
    isActive(route) {
      return route.path === this.$route.path
    },
    isAffix(route) {
      return route.meta && route.meta.affix
    },
    addView() {
      const route = this.$route
      if (!route.meta || !route.meta.title) return
      const hasView = this.visitedViews.some(v => v.path === route.path)
      if (!hasView) {
        this.visitedViews.push({
          path: route.path,
          meta: { ...route.meta }
        })
      }
      if (this.visitedViews.length > 20) {
        this.visitedViews.shift()
      }
    },
    closeTag(view) {
      const index = this.visitedViews.findIndex(v => v.path === view.path)
      if (index === -1) return
      this.visitedViews.splice(index, 1)
      if (this.isActive(view)) {
        const lastView = this.visitedViews.slice(-1)[0]
        if (lastView) {
          this.$router.push(lastView.path)
        } else {
          this.$router.push('/')
        }
      }
    },
    openMenu(tag, e) {
      this.closeTag(tag)
    }
  }
}
</script>

<style scoped>
.tags-view {
  height: 34px;
  background: #f0f2f5;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);
}
.tags-view-wrapper {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 10px;
  overflow-x: auto;
}
.tags-view-item {
  display: inline-flex;
  align-items: center;
  height: 26px;
  line-height: 26px;
  padding: 0 8px;
  margin: 0 2px;
  font-size: 12px;
  color: #495060;
  background: #fff;
  border: 1px solid #d8dce5;
  border-radius: 2px;
  text-decoration: none;
  white-space: nowrap;
}
.tags-view-item.active {
  color: #fff;
  background: #409EFF;
  border-color: #409EFF;
}
.tags-view-item-close {
  margin-left: 4px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 50%;
  width: 14px;
  height: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.tags-view-item-close:hover {
  background: rgba(0, 0, 0, 0.1);
}
</style>
