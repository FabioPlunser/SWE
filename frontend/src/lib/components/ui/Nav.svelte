<script lang="ts">
  import Logo from "$assets/Logo.svg";
</script>

<!-- @component
Navbar / header component

Usage:
  ```tsx
<Nav/>
``` 
-->

<nav class="navbar">
  <div class="flex">
    <a href="/" class="">
      <img alt="PlantHealth" src={Logo} class="w-auto h-auto" />
    </a>
  </div>
</nav>
