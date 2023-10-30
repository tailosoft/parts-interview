import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Part',
    route: '/part',
    translationKey: 'global.menu.entities.part',
  },
  {
    name: 'Assembly',
    route: '/assembly',
    translationKey: 'global.menu.entities.assembly',
  },
  {
    name: 'Supplier',
    route: '/supplier',
    translationKey: 'global.menu.entities.supplier',
  },
  {
    name: 'PartSupplier',
    route: '/part-supplier',
    translationKey: 'global.menu.entities.partSupplier',
  },
];
