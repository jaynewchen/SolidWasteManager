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
  height: 36px;
  background: #F8FAFC;
  border-bottom: 1px solid #E2E8F0;
  box-shadow: 0 1px 2px rgba(79,70,229,0.04);
}
.tags-view-wrapper {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 12px;
  overflow-x: auto;
}
.tags-view-item {
  display: inline-flex;
  align-items: center;
  height: 28px;
  line-height: 28px;
  padding: 0 10px;
  margin: 0 3px;
  font-size: 12px;
  font-weight: 500;
  color: #64748B;
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
  text-decoration: none;
  white-space: nowrap;
  transition: all 0.2s ease;
}
.tags-view-item:hover {
  color: #4F46E5;
  border-color: rgba(79,70,229,0.3);
}
.tags-view-item.active {
  color: #4F46E5;
  background: rgba(79,70,229,0.06);
  border-color: rgba(79,70,229,0.2);
}
.tags-view-item-close {
  margin-left: 4px;
  font-size: 10px;
  cursor: pointer;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}
.tags-view-item-close:hover {
  background: rgba(79,70,229,0.1);
  color: #4F46E5;
}
</style>
